# sshd-test
Test code for apache mina sshd to demonstrate retrieval truncation issue


There's a gradle task to demonstrate the issue: `./gradlew lftpDownload`

The file `data/test-download.data` can be used to reproduce the problem.
