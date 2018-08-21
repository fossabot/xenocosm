# Contributor guide

## About this document

This guide is for people who would like to be involved in building Xenocosm. It
began life as a copy of [typelevel/cats#CONTRIBUTING.md](https://github.com/typelevel/cats/blob/2e781ae32fc35fee49f5b4f8b8cc675db3ed7eee/CONTRIBUTING.md).

This guide assumes that you have some experience doing Scala development. If you
get stuck on any of these steps, please feel free to [ask for help](#getting-in-touch).

## How can I help?

Xenocosm follows a standard
[fork and pull](https://help.github.com/articles/using-pull-requests/) model for
contributions via GitHub pull requests.

Below is a list of the steps that might be involved in an ideal contribution. If
you don't have the time to go through every step, contribute what you can, and
someone else will probably be happy to follow up with any polishing that may
need to be done.

If you want to touch up some documentation or fix typos, feel free to skip these
steps and jump straight to submitting a pull request.

 1. [Find something that belongs in xenocosm](#find-something-that-belongs-in-xenocosm)
 2. [Let us know you are working on it](#let-us-know-you-are-working-on-it)
 3. [Build the project](#build-project)
 4. [Implement your contribution](#write-code)
 5. [Write tests](#write-tests)
 6. [Write documentation](#write-documentation)
 7. [Write examples](#write-examples)
 8. [Submit pull request](#submit-a-pull-request)

### Find something that belongs in xenocosm

Looking for a way that you can help out? Check out our [open issues](https://github.com/robotsnowfall/xenocosm/issues)
and look for ones tagged as _**help wanted**_ or _**low-hanging fruit**_. These
issues are the easiest way to start contributing, but if you find other items
that catch your eye, you're most than welcome to tackle them!

Make sure that it's not already assigned to someone and that nobody has left a
comment saying that they are working on it!

(Of course, you can also comment on an issue someone is already working on and
offer to collaborate.)

Have an idea for something new? That's great! We recommend that you make sure it
belongs in Xenocosm before you put effort into creating a pull request. The
preferred way to do that are to [create a GitHub issue](https://github.com/robotsnowfall/xenocosm/issues/new)
describing your idea.

Xenocosm (and especially `xenocosm-core`) is intended to be lean and modular.
Some great ideas are not a great fit, either due to their size or their
complexity. In these cases, creating your own library that depends on Xenocosm
is probably the best plan.

### Let us know you are working on it

If there is already a GitHub issue for the task you are working on, leave a
comment to let people know that you are working on it. If there isn't already an
issue and it is a non-trivial task, it's a good idea to create one (and note
that you're working on it). This prevents contributors from duplicating effort.

### Build the project

First you'll need to checkout a local copy of the code base:

```sh
git clone git@github.com:robotsnowfall/xenocosm.git
```

To build Xenocosm you should have
[sbt](https://www.scala-sbt.org/1.x/docs/Setup.html) installed. Run `sbt`, and
then use any of the following commands:

 * `compile`: compile the code
 * `console`: launch a REPL
 * `test`: run the tests
 * `unidoc`: generate the documentation
 * `scalastyle`: run the style-checker on the code
 * `validate`: run tests, style-checker, and doc generation

**IntelliJ**

 * Be warned, IntelliJ is currently not 100% accurate at reporting compilation
   errors, there *will* be cases that it reports errors incorrectly. If you
   simply don't want to see the errors, a quick an easy work around is to
   disable *Type-Aware Highlighting* by clicking the `[T]` icon in the bottom
   toolbar.

 * There is an open [issue](https://github.com/typelevel/cats/issues/2152) with
   the IntelliJ scala plugin, which prevents it from configuring similacrum
   correctly when importing the cats project, which is a dependency for Xenocosm.

 * IntelliJ does have [support](https://blog.jetbrains.com/scala/2015/07/31/inline-refactoring-for-type-aliases-and-kind-projector-support/)
   for kind-projector. However, it is not always seamless. If you are unable to
   get IntelliJ to recognise the special symbols that kind-project provides,
   such as `?` `Lambda[X => G[F[A]]]` or `λ[X => G[F[A]]]` try upgrading to the
   early access preview (EAP) version of the scala plugin. This can be done
   under `Settings > Languages & Frameworks > Scala > Updates`

### Write code

You know what to do.

### Attributions

If your contribution has been derived from or inspired by other work, please
state this in its ScalaDoc comment and provide proper attribution. When possible,
include the original authors' names and a link to the original work.

### Write tests

 * Xenocosm tests should extend `XenocosmSuite`. `XenocosmSuite` integrates
   [ScalaTest](http://www.scalatest.org/) with [Discipline](https://github.com/typelevel/discipline)
   for law checking, and imports all syntax and standard instances for
   convenience.

## Contributing documentation

### Source for the documentation

The documentation for this website is stored alongside the source, in the
[docs subproject](https://github.com/robotsnowfall/xenocosm/tree/master/docs).

* The source for the tut compiled pages is in `docs/src/main/tut`
* The menu structure for these pages is in `docs/src/main/resources/microsite/data/menu.yml`

### Generating the Site

run `sbt docs/makeMicrosite`

### Previewing the site

1. Install jekyll locally. Depending on your platform, you might do this with:

    `yum install jekyll`

    `apt-get install ruby-full; gem install jekyll`

    `gem install jekyll`

2. In a shell, navigate to the generated site directory in `docs/target/site`

3. Start jekyll with `jekyll serve`

4. Navigate to http://localhost:4000/xenocosm/ in your browser

5. Make changes to your site, and run `sbt docs/makeMicrosite` to regenerate the site. The changes should be reflected as soon as you run `makeMicrosite`.

### Compiler verified documentation

We use [tut](https://github.com/tpolecat/tut) to compile source code which
appears in the documentation, this ensures us that our examples should always
compile, and our documentation has a better chance of staying up-to-date.

### Write examples

One of the best ways to provide examples is doctest, here is
[an example](https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/Functor.scala#L19-L33).
Doctest is a [sbt plugin](https://github.com/tkawachi/sbt-doctest) which
generates tests based on the syntax mentioned above and runs when sbt's `test`
task is invoked. You can find more information in the plugin documentation.

### Submit a pull request

Before you open a pull request, you should make sure that `sbt validate` runs
successfully. Travis will run this as well, but it may save you some time to be
alerted to style problems earlier.

If your pull request addresses an existing issue, please tag that issue number
in the body of your pull request or commit message. For example, if your pull
request addresses issue number 52, please include "fixes #52".

If you make changes after you have opened your pull request, please add them as
separate commits and avoid squashing or rebasing. Squashing and rebasing can
lead to a tidier git history, but they can also be a hassle if somebody else has
done work based on your branch.

## How did we do?

Getting involved in an open source project can be tough. As a newcomer, you may
not be familiar with coding style conventions, project layout, release cycles,
etc. This document seeks to demystify the contribution process for the Xenocosm
project.

It may take a while to familiarize yourself with this document, but if we are
doing our job right, you shouldn't have to spend months pouring over the project
source code before you feel comfortable contributing. In fact, if you encounter
any confusion or frustration during the contribution process, please create a
GitHub issue and we'll do our best to improve the process.

## Getting in touch

Discussion around Xenocosm is currently happening on Github issue and PR pages.
You can get an overview of who is working on what via the [projects page](https://github.com/robotsnowfall/xenocosm/projects).

Feel free to open an issue if you notice a bug, have an idea for a feature, or
have a question about the code. Pull requests are also gladly accepted.

People are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html)
when discussing Xenocosm on the Github page or other venues.

We hope that our community will be respectful, helpful, and kind. If you find
yourself embroiled in a situation that becomes heated, or that fails to live up
to our expectations, you should disengage and contact one of the [project maintainers](https://github.com/robotsnowfall/xenocosm#implementors)
in private. We hope to avoid letting minor aggressions and misunderstandings
escalate into larger problems.

If you are being harassed, please contact one of
[us](https://github.com/robotsnowfall/xenocosm#implementors) immediately so that
we can support you.
