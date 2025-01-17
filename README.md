![Hillview project logo](hillview-logo.png)

# Introduction

Hillview: a big data spreadsheet.  Hillview is a cloud-based
application for browsing large datasets.  The hillview user interface
executes in a browser.  Currently the software is alpha quality, under
active development.

# Documentation

There is a [Hillview user manual](docs/userManual.md).

A [short video](https://1drv.ms/v/s!AlywK8G1COQ_jeRQatBqla3tvgk4FQ)
shows the system in action in real-time.

You can [test](http://ec2-18-217-136-170.us-east-2.compute.amazonaws.com:8080/)
a demo of the system running on 15 small Amazon machines.

A [paper](https://arxiv.org/abs/1907.04827) describing the system in
some detail.  This is an extended version of the following publication
Mihai Budiu, Parikshit Gopalan, Lalith Suresh, Udi Wieder, Han
Kruiger, and Marcos K. Aguilera, Hillview: A trillion-cell spreadsheet
for big data, in PVLDB 2019, 12(11).

Documentation for the [internal APIs](docs/hillview-apis.pdf).

# Developing Hillview

## Software Dependencies

* Back-end: Ubuntu Linux > 16 or MacOS
* Java 8, Maven build system, various Java libraries
  (Maven will manage the libraries)
* Front-end: Typescript, webpack, Tomcat app server, node.js;
  some JavaScript libraries: d3, pako, and rx-js
* Cloud service management: Python3
* IDEA Intellij for development (optional)

## Pre-built binaries

The release contains a [tar
file](https://github.com/vmware/hillview/releases/download/v0.5-alpha/hillview-bin.taz)
with pre-built binaries.  You should untar this file in the toplevel
directory of hillview.  This archive does not contain the Apache
Tomcat web server; to get that one you need to [install the
dependencies.](#software-needed-for-deployment)

## Building the binaries from source

* First install all software required as described
  [below](#software-needed-for-deployment).

* Build the software:

```
$ cd bin
$ ./rebuild.sh -a
```

### Build details

Hillview is currently split into two separate Maven projects.

* platform: pure Java, includes the entire back-end.  `platform` can
be developed using the free (community edition) of Intellij IDEA.
This produces a JAR file `platform/target/hillview-jar-with-dependencies.jar`.
This part can be built with:

```
$ cd platform
$ mvn clean install
$ cd ..
```

* web: the web server, web client and web services; this project links
to the result produced by the `platform` project.  This produces a WAR
(web archive) file `web/target/web-1.0-SNAPSHOT.war`.  This part can
be built with:

```
$ cd web
$ mvn package
$ cd ..
```

## Single-machine development and testing

These instructions describe how to run hillview on a single machine
using a sample dataset.

* Download and prepare the sample data.  The download script will
  download and decompress some CSV files with flights data from FAA.
  You can edit the program `data/ontime/download.py` to change the
  range of data that will be downloaded; the default is to download 2
  months of data.  The dataset has 110 columns; we can use them all,
  but for the demo we have stripped the dataset to 15 columns to
  better fit on the screen.  The following command creates the smaller
  files from the downloaded data; this has to be done only once, after
  downloading the data.

```
$ ./demo-data-cleaner.sh
```

* Next start the back-end service which performs all the data
  processing:

```
$ ./backend-start.sh &
```

* Start the web server which receives requests from clients and
dispatches them to the back-end servers; note that the folder where
this command is run is important, since the path to the data files is
relative to this folder.

```
$ ./frontend-start.sh
```

* start a web browser at http://localhost:8080

* when you are done stop the two services by killing the
  frontend-start.sh and backend-start.sh jobs.

# Deploying the Hillview service on a cluster

Hillview uses `ssh` to deploy code on the cluster.  Prior to
deployment you must setup `ssh` on the cluster to use password-less
access to the cluster machines, as described here:
https://www.ssh.com/ssh/copy-id

*Please note that Hillview allows arbitrary access to files on the
worker nodes from the client application.  The worker nodes should be
deployed within a restricted secure environment (e.g. containers).*

Before you run these commands, make sure you've built both `platform`
and `web` projects.  The deployment scripts are in the `bin`
folder.

```
$: cd bin
```

## Service configuration

The fixed configuration of the Hillview service is obtained from a
configuration file; there is a sample file `bin/config.json`.

```JSON
// This file is a Json file that defines the configuration for a
// Hillview deployment.

{
  // Name of machine hosting the web server
  "webserver": "web.server.name",
  // Names of the machines hosting the workers; the web
  // server machine can also act as a worker
  "aggregators": [
    // The "aggregators" level is optional; if it is
    // missing the configuration should contain just an array of workers
    {
      "name": "aggregator1.name",
      "workers": [
        "worker1.name",
        "worker2.name"
      ]
    }, {
      "name": "aggregator2.name",
      "workers": [
        "worker3.name",
        "worker4.name"
      ]
    }
  ],
  // Network port where the workers listen for requests
  "worker_port": 3569,
  // Network port where aggregators listen for requests
  "aggregator_port": 3570,
  // Java heap size for Hillview workers
  "default_heap_size": "25G",
  // User account for running the Hillview service
  "user": "hillview",
  // Folder where the hillview service is installed on remote machines
  "service_folder": "/home/hillview",
  // Version of Apache Tomcat to deploy
  "tomcat_version": "9.0.4",
  // Tomcat installation folder name
  "tomcat": "apache-tomcat-9.0.4",
  // If true delete old log files
  "cleanup": false,
  // This can be used to override the default_heap_size for specific machines.
  "workers_heapsize": {
    "worker1.name": "25G"
  }
}
```

## Deployment scripts

The following command installs the software on the machines:

```
$: deploy.py config.json
```

The service is started by running the following command:

```
$: start.py config.json
```

To connect to the service open `http://<webserver>:8080` in your web
browser.

To stop the services you can run:

```
$: stop.py config.json
```

# Contributing code

You will need to sign a CLA (Contributor License Agreement) to
contribute code to Hillview under an Apache-2 license.  This is very
standard.

## Setup IntelliJ IDEA

Download and install Intellij IDEA: https://www.jetbrains.com/idea/.
You can just untar the linux binary in a place of your choice and run
the shell script `ideaXXX/bin/idea.sh`.  The web projects uses
capabilities only available in the paid version of Intellij IDEA.

## Loading into IntelliJ IDEA

One solution is to load only the module that you want to contribute to: move to the
corresponding folder: `cd platform` or `cd web` and start
intellij there.

Alternatively, if you have IntelliJ Ultimate you can create an empty project
in the hillview folder, and then import three modules (from File/Project structure/Modules,
add three modules: web/pom.xml, platform/pom.xml, and the root folder hillview itself).

## Using git to contribute

* Fork the repository using the "fork" button on github, by following these instructions:
https://help.github.com/articles/fork-a-repo/
* Run IntelliJ code inspection (Analyze/Inspect code) before commit and solve all open issues.
* Submit them into your own forked repository and send us a pull request.

In more detail, here is a step-by-step guide to committing your changes:

1. Create a new branch for each fix; give it a nice suggestive name:
   - `git branch yourBranchName`
   - `git checkout yourBranchName`
   - The main benefit of using branches is that you can have multiple
     branches active at the same time, one for each independent fix.
2. `git add <files that changed>`
3. `git commit -m "Description of commit"`
4. `git fetch upstream`
5. `git rebase upstream/master`
6. Resolve conflicts, if any
   (rebase won't work if you don't; as you find conflicts you will need
    to `git add` the files you have merged, and then you may need to use
    `git rebase --continue` or `git rebase --skip`)
7. Test, analyze merged version.
8. `git push -f origin yourBranchName`.  You won't need the `-f` if you are
   not updating a previous push to this branch.
9. Create a pull request to merge your new branch into master (using the web ui).
10. Delete your branch after the merging has been done `git branch -D yourBranchName`
11. To run the program you should try the master branch:
  - `git checkout master`
  - `git fetch upstream`
  - `git rebase upstream/master`
  - `git push origin master`

## Guidance in writing code

* The pseudorandom generator is implemented in the class
  Randomness.java and uses Mersenne Twister.  Do not use the
  Java `Random` class, but this one.

* By default all pointers are assumed to be non-null; use the
  @Nullable annotation (from javax.annotation) for all pointers which
  can be null.  Use `Converters.checkNull` to cast a @Nullable to a
  @NonNull pointer.

# Software needed for deployment

## Installing Java

We use Java 8; newer versions will *not* work.

First, download a JDK (for Linux x64 or MacOS) from here:
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
Note: it is not enough to have a Java VM installed, you need a JDK.

Make sure to download the tarball version of the JDK.

For Linux: Unpack the JDK, and set your `JAVA_HOME` environment variable to point
to the unpacked folder (e.g, <fully qualified path
to>/jdk/jdk1.8.0_101). To set your JAVA_HOME environment variable, add
the following to your ~/.bashrc or ~/.zshrc.

```
$ export JAVA_HOME="<path-to-jdk-folder>"
```

(For MacOS you do not need to set up JAVA_HOME.)

## Installing other software needed

The following shell script will install the other required dependencies
for building and testing.

On MacOS you first need to install [Homebrew](https://brew.sh/).  One way to do that is to run
```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

To install all other dependencies you can run:

```
$ cd bin
$ ./install-dependencies.sh
```

For old versions of Ubuntu this may fail, so you may have to install the required
dependencies manually.

## Impala Java libraries

If you want to access the [Impala](https://impala.apache.org/)
database you will need to download and install the JDBC connectors for
Impala libraries from
[Cloudera](https://www.cloudera.com/documentation/other/connectors.html).
(These are not free software, so they are not available in Java Maven
repositories.)  You should install these in your local Maven
repository, e.g. in the ~/.m2/com/cloudera/impala folder.  You may
also need to adjust the version of the libraries in the file
platform/pom.xml.
