<h1 align="center">
  MFStock
</h1>

**MFStock** application for operation of a data collection terminal with a local database stored on a computer

## Get started
1. Take MFStock.jar from the distribution.
2. Run `java -jar MFStock.jar --generateDBConfigs` to generate the database config file.
3. Fill database config file
4. Run application `java -jar MFStock.jar --port=[port]`

## Usage
| Command                                             | Description                                                   |
|-----------------------------------------------------|---------------------------------------------------------------|
| --help                                              | Show help info                                                |
| --port=[port]                                       | Setup server port                                             |
| --dbPath=[path to database]                         | Setup path to database                                        |
| --dbConfigsPath=[path to database config file]      | Setup path to database config file (default ./DBConfigs.json) |
| --generateDBConfigs                                 | Generate the database config file                             |

## License
MFStock is licensed under the [GNU GENERAL PUBLIC](LICENSE.md) license.