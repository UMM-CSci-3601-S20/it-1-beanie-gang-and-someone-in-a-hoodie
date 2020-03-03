# CSCI 3601 Iteration Template

[![Server Build Status](../../workflows/Server%20Java/badge.svg)](../../actions?query=workflow%3A"Server+Java")
[![Client Build Status](../../workflows/Client%20Angular/badge.svg)](../../actions?query=workflow%3A"Client+Angular")
[![End to End Build Status](../../workflows/End-to-End/badge.svg)](../../actions?query=workflow%3AEnd-to-End)

This is your starter code for Iteration 1.

There are a number of pieces in this production template to help you get started. As you work on your project, you should replace some of these pieces with elements of your project and remove whatever you don't need (e.g., markdown files, JSON data files, or any remnants of the labs).

## Setup

As in the previous lab, you'll be using VS Code and GitKraken. Once you've all joined your
group using GitHub classroom, you can clone your repository using the command line or GitKraken:

- From the file menu, choose **Clone Repo**
- Choose GitHub.com in the middle column (as the source location of your repo)
- Browse to the location you'd like to put the local copy of this project repo
- Select the correct repo from the list of repositories
- Select **Clone the repo!**


## Running your project

- The **run** Gradle task (`./gradlew run` in the `server` directory) will still run your Javalin server, which is available at [`localhost:4567`](http://localhost:4567).
- The **build** task will still _build_ the server, but not run it.

Like in lab 3, the first time you run your Angular project, you will need to run move into your `client` directory and run `npm install` so that all the dependencies managed by npm will be installed. 

Once you have successfully run `npm install`, in order to serve up the _client side_ of your project, you will type 
`ng serve` and the client will be running at [`localhost:4200`](http://localhost:4200).

The major difference between this lab and lab #3 is that, here, your data (users and todos) will be stored in a database rather than as "flat" JSON files within the server source code.

For the most part, you will be using a local installation of Mongo as a dev (development) database. You don't really need to worry about how this is set up, but you do need to know a couple of tricks to help you use it:

### Seeding the Database

To give yourself some data to work with instead of starting with an empty database in our development environment, you need to 'seed' the database with some starter data. Seed data and the seed script are stored in the top level directory `database`. To seed the database, move into that directory and run `./mongoseed.sh`. This will take each of the JSON files in `database/seed/` and insert their elements into the `dev` database (to specify a different database, provide it as an argument). It also drops the database before seeding it so it is clean.

### MongoDB in VS Code

We have included an extension called [Azure Cosmos DB](https://marketplace.visualstudio.com/items?itemName=ms-azuretools.vscode-cosmosdb) in the recommended extensions. This extension allows you to view, edit, and delete the things in MongoDB.

When installed you will see a new icon in the sidebar, click it and click "Attach Database Account...".

![](https://i.vgy.me/ElqdfW.png)

Then select "Azure Cosmos DB for MongoDB API".

It will ask you for a connection string, hitting enter on the default one should work for the machines in our lab.

![](https://i.vgy.me/2dk1ws.png)

You will then have the MongoDB server in the sidebar. You can explore the databases and collections here. You can click a record to view and edit it or right click it for other options like deleting. You can also import JSON into the database right from this extension.

![](https://i.vgy.me/AWAUHw.png)

## Testing and Continuous Integration

There are now more testing options! You can test the client, or the server or both.

### Testing the client

From the `client` directory:
* `ng test` runs the client tests.
* `ng test --code-coverage` runs the client tests and generates a coverage file you can find in your client directory `client/coverage/client/index.html`.
Right click on `index.html` and select `Copy path` and paste it into your browser of choice. For Chrome users, you can drag and drop `index.html` onto the tab area of Chrome and it will open it.

### Testing the server

From the `server` directory:
* `./gradlew test` runs the server tests once.
* `./gradlew test jacocoTestReport` runs the server tests and generates a coverage file you can find in `server/build/jacocoHtml/index.html`.

### End to end testing
* `npm run e2e` from the `client` directory runs end to end tests. 
  * What are e2e tests? They are tests that run the real application and simulate user behavior. They assert that the app is running as expected. 
  * NOTE: The server (`./gradlew run` in the `server` directory) needs to be actively executing for these tests to work!

### GitHub Actions

There are GitHub Actions set up in your repo for each of the three checks: JUnit tests for the server, Karma tests for the client, and Protractor tests for end-to-end testing. There are badges above that show the status of these checks on the master branch.

## Changing the name

The project by default has the name "CSCI 3601 Iteration Template". There are a few places you need to change to make this the name you want:

- The title of this README.md
- [`server/src/main/java/umm3601/Server.java`](server/src/main/java/umm3601/Server.java)
  - The `appName` variable
- [`client/src/app/app.component.ts`](client/src/app/app.component.ts)
  - The `title` variable
  - Also the associated unit and E2E tests will need to be changed.
- [`client/src/index.html`](client/src/index.html)
  - The `title` element

## Deployment

Instructions on how to crate a DigitalOcean Droplet and setup your project are in [DEPLOYMENT.md](DEPLOYMENT.md).

## Resources

- [Angular Material components docs](https://material.angular.io/components/categories)
- [Angular tutorial][angular-tutorial]
- [Angular testing (Karma)][angular-karma]
- [Angular Tour of Heroes tutorial][tour-of-heroes]
- [End-to-end testing (Protractor)][protractor]
- [End to end testing (e2e) with protractor and Angular CLI][e2e-testing]
- [What are environments in Angular CLI?][environments]
- [Angular CLI commands][angular-cli-commands]
- [HTTP Status Codes][status-codes]
- [MongoDB Java Driver][mongo-java]

[angular-tutorial]: https://angular.io/start
[angular-karma]:https://angular.io/guide/testing
[tour-of-heroes]: https://angular.io/tutorial
[protractor]: https://www.protractortest.org/#/toc
[e2e-testing]: https://coryrylan.com/blog/introduction-to-e2e-testing-with-the-angular-cli-and-protractor
[environments]: https://angular.io/guide/build#configuring-application-environments
[labtasks]: LABTASKS.md
[angular-cli-commands]: https://angular.io/cli
[status-codes]: https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
[mongo-java]: https://mongodb.github.io/mongo-java-driver/

