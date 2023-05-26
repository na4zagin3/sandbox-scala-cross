## Example sbt project cross-compiled with JS, JVM, and Native

### Usage

This is a normal sbt project, you can compile code with `sbt compile` and run it
with `sbt run`, `sbt console` will start a Dotty REPL. For more information on
cross-compilation in sbt, see <https://www.scala-sbt.org/1.x/docs/Cross-Build.html>.

### Create NPM package

```sbt
> clean
> parserJS/npmPackageNpmrc
> parserJS/npmPackage
```

```sh
$ cd parser/js/target/scala-3.2.2/npm-package
$ npm link
```
