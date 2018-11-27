package com.github.romassistant.handler;

import com.github.romassistant.MessageController;
import com.github.romassistant.model.Ruin;
import com.github.romassistant.model.User;
import com.github.romassistant.model.db.cache.RuinDbCache;
import com.github.romassistant.model.db.cache.UserDbCache;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@ConditionalOnBean(MessageController.class)
public class RuinHandler extends TextMessageEventHandler {
    private static final Log log = LogFactory.getLog(RuinHandler.class);

    @Autowired
    private LineMessagingClient client;
    @Autowired
    private UserDbCache userDbCache;
    @Autowired
    private RuinDbCache ruinDbCache;

    private String naReplyText; // If no bosses info is available
    private String newReplyText; // If someone is reporting the bosses info
    private Set<String> keywords;
    private Set<String> keywords2;
    private Pattern pattern = Pattern.compile("(\\d+)(.*)");


    public RuinHandler(
            @Value("${handler.RuinHandler.naReplyText}") String naReplyText,
            @Value("${handler.RuinHandler.newReplyText}") String newReplyText,
            @Value("${handler.RuinHandler.keywords}") String[] keywords,
            @Value("${handler.RuinHandler.keywords2}") String[] keywords2) {
        this.naReplyText = naReplyText;
        this.newReplyText = newReplyText;
        this.keywords = new HashSet<>(Arrays.asList(keywords));
        this.keywords2 = new HashSet<>(Arrays.asList(keywords2));
    }

    @Override
    public boolean isApplicable(String text) {
        if (keywords.contains(text)) {
            return true;
        }

        Matcher matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            return false;
        }

        String text2 = matcher.group(2);
        for (var keyword : keywords2) {
            if (text2.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(Event e) {
        var event = (MessageEvent<TextMessageContent>)e;
        var message = new TextMessage(getReplyMessage(parse(event)));
        var future = client.replyMessage(new ReplyMessage(event.getReplyToken(), message));

        try {
            var response = future.get();
            log.debug(response);
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ParseResult parse(MessageEvent<TextMessageContent> e) {
        Matcher matcher = pattern.matcher(e.getMessage().getText().trim());
        if (!matcher.matches()) {
            throw new IllegalStateException("Wrong handler is used.");
        }

        int level = Integer.parseInt(matcher.group(1).trim());
        String text2 = matcher.group(2).trim(); // the text following the level
        if (!text2.isEmpty()) {
            if (!keywords2.contains(text2)) { // false if the text is a query (eg. "100EG")
                // The text is for reporting bosses information.
                // We expect there is a whitespace following the keyword.
                String bosses = text2.substring(text2.indexOf(" ")).trim();
                log.info(String.format("New report: {Lv=%s; Bosses=%s}", level, bosses));
                return new ParseResult(e, ParseResult.REPORT, level, bosses);
            }
        }

        return new ParseResult(e, ParseResult.QUERY, level, null);
    }

    private String getReplyMessage(ParseResult result) {
        switch (result.type) {
            case ParseResult.QUERY:
                var ruins = getRuinInfo(result.level);
                if (ruins.size() != 0) {
                    return formatRuinInfoList(ruins);
                } else {
                    return naReplyText;
                }
            case ParseResult.REPORT:
                updateRuinInfo(result);
                return newReplyText;
            default:
                throw new RuntimeException();
        }
    }

    private List<Ruin> getRuinInfo(int level) {
        return ruinDbCache.readAll().stream()
                .filter(ruin -> ruin.getLevel()==level)
                .collect(Collectors.toList());
    }

    private void updateRuinInfo(ParseResult result) {
        var event = result.event;
        var user = userDbCache.read(event.getSource().getUserId());
        var reporter = user.map(User::getId).orElse(null);
        ruinDbCache.add(new Ruin(result.level, result.bosses, event.getTimestamp(), reporter));
    }

    private String formatRuinInfoList(List<Ruin> ruins) {
        StringBuilder str = new StringBuilder();
        for (var ruin : ruins) {
            var zone = ZoneId.of("UTC+8");
            str.append(String.format("[%d遺跡] %s (%s) ",
                    ruin.getLevel(), ruin.getBosses(),
                    ruin.getTime().atZone(zone).format(DateTimeFormatter.ISO_LOCAL_DATE)));

            ruin.getReporter().ifPresent(
                    uid -> userDbCache.read(uid).ifPresent(
                    user -> str.append(String.format("感謝勇者 %s 的回報！",
                            user.getNickname().orElse(user.getName())))));

            str.append(System.lineSeparator());
        }

        return str.toString();
    }


    private static class ParseResult {
        static final int QUERY  = 0x1;
        static final int REPORT = 0x1 << 1;

        Event event;
        int type;
        int level;
        String bosses;

        ParseResult(Event event, int type, int level, String bosses) {
            this.event = event;
            this.type = type;
            this.level = level;
            this.bosses = bosses;
        }
    }
}
