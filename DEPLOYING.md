# Deploying Instructions

These instructions are based on the [instructions](http://central.sonatype.org/pages/ossrh-guide.html)
for deploying to the Central Repository using [Maven](http://central.sonatype.org/pages/apache-maven.html).

You will need the following:
- Sonatype username and password
  - You need to register with the Sonatype jira
  - Create an issue asking for permission, referencing Noa Resare, using this: [~noa]. Compare [OSSRH-11554](https://issues.sonatype.org/browse/OSSRH-11554)
  - You should perhaps also talk to Noa using some other channel, IRC or email.
  - lastly, you need to add these credentials to your `settings.xml` as `sonatype-nexus-snapshots`
- [GPG set up on the machine you're deploying from](http://central.sonatype.org/pages/working-with-pgp-signatures.html)

Once you've got that in place, you should be able to do deployment using the following commands:

```
# snapshot version
mvn clean deploy

# make and deploy a relase
mvn release:clean release:prepare
mvn release:perform
```
