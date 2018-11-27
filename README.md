# RO-Mobile Assistant (RO小幫手)

An assistant bot for the Mobile game [RO仙境傳說：守護永恆的愛](https://rom.gnjoy.com.tw/).

**Features**:

- Automatically record and report the bosses information for the guild ruin by keywords.
- A scheduler which pushes notification messages. The day of week, time, period between each
  notification and messages are customizable via a json file.
  
## Prerequisites

- **JDK 10 or above**
- **Line application channel**  
  The application uses the Line Messaging API, and hence you need to have a
  [Line application channel](https://developers.line.biz/en/docs/messaging-api/getting-started/)
  to obtain the token and secret of the channel for using the Line API.
- **A server which supports https to host the bot**  
  The Line webhook URL requires https. It is recommended to use [Heroku](https://www.heroku.com),
  a cloud platform for deploying and manages your apps. You can refer to
  [this article](https://developers.line.biz/en/docs/messaging-api/building-sample-bot-with-heroku/)
  for more information.
- **A relational database**  
  Heroku has a plugin that provides managed SQL database service. See
  [Heroku Postgres](https://devcenter.heroku.com/articles/heroku-postgresql) for more information.  
  - The table schema is described [here](https://github.com/smilecatx3/ROM-Assistant/wiki/Database-Table-Schema).

>NOTE: All the prerequisites listed above are only required by the *Line message service* of the
>application that can be optionally disabled, and therefore they can be omitted if only the
>*scheduler service* is needed.

## Build the Project

Build the project by the command `./gradlew build`. By default Gradle will build an executable
jar file to the *build/libs* directory.  
To enable the *Line message service*, set the Gradle project property `service.line.message.enabled=true`
in *gradle.properties*.

## Execution

Deploy the application to Heroku with a Procfile for execution. For manully execution please
refer to the command in Procfile.

### Application properties

To use the *Line message service*, you need to set the following properties in the
*application.properties* file.

```ini
service.line.message.enabled=true
db.enabled=true
line.bot.channel-secret=YOUR_LINE_BOT_CHANNEL_SECRET
line.bot.channel-token=YOUR_LINE_BOT_CHANNEL_TOKEN
line.bot.handler.path=/callback
```

For the Line bot channel secret and token, you can instead set the environment variables
`LINE_BOT_CHANNEL_SECRET` and `LINE_BOT_CHANNEL_TOKEN`, respectively.

Also, you need to set an environment variable `DATABASE_URL`. The application uses Postgres as
the JDBC driver and the database URI parsing is dedicated to this; therefore, if you wish to adopt
another JDBC driver, you need to modify the URI parsing method in the DatabaseConfig class.

## Licensing

The project is distributed under the MIT license.
