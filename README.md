<h1 align="center">
  MFStock
</h1>

**MFStock** application for operation of a data collection terminal with a local database stored on a computer

## Get started
1. Take MFStock.jar from the distribution.
2. Run `java -jar MFStockServer-[version].jar --mfstock.database.path=[path to database]` to run application.

## Usage
| Command                                                                    | Description                                                          |
|----------------------------------------------------------------------------|----------------------------------------------------------------------|
| --server.port=[port]                                                       | Setup server port                                                    |
| --mfstock.database.path=[path to database]                                 | Setup path to database                                               |
| --mfstock.config.path=[path to server config file]                         | Setup path to database config file (default ./DBConfigs.json)        |
| --mfstock.sticker.generated.folder.path=[path to generated sticker folder] | Setup path to generated sticker folder (default ./generated_sticker) |

## License
MFStock is licensed under the [GNU GENERAL PUBLIC](LICENSE.md) license.