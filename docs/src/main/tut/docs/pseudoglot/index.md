---
layout: docs
title: Pseudoglot
---
# Building a Name Generator with Pseudoglot

For our purposes, a name generator is a program that will generate syllables
which should appear to more-or-less "belong" together and, when "romanized",
give a rough feeling of a proper name. We do that in Pseudoglot by building up
a series of generators starting at the level of the phoneme, continuing on to
morphemes, and culminating in onomastics.

## Setup our imports and create the RNG

Pseudoglot uses the `spire.random` package for generating `Dist[T]` instances,
which in turn define how random components of our name generator are created. So
the first thing we need is a (pseudo-)random number generator. We'll just use
the default one here, but Spire lets you choose from an assortment.

```tut
import cats.implicits._
import spire.random.Dist
import pseudoglot.data._
import pseudoglot.implicits._

val rng = spire.random.Generator.rng
```

## Create a random Phonology

Next up, we'll create a `Phonology` which encapsulates a phonemic catalog or
inventory, together with rules for how they phones fit together, called
phonotactics.

```tut
val phonology = Phonology.dist(rng)
```

## Provide a transcription

Pseudoglot provides an instance of `Transcription` for the International
Phonetic Alphabet. However, one often needs a romanization (phonemic
transcription) of `Phones` into a non-academic orthography for practical use. As
such, Pseudoglot also provides a way of generating a `Transcription` with
`pseudoglot.Romanization.dist`. This creates a randomized set of romanization
strategies on top of a base drawn from the _Handbook of the IPA_.

```tut
implicit val xlit = pseudoglot.Romanization.dist(rng)
```

## Transcribe the phonology

Now with that bookkeeping out of the way, lets see what we have. First, we'll
generate an alphabet (of sorts), which simply transcribes each `Phone` in our
`Phonology` using the above `Transcription`. Since our transcription likely
doesn't provide a mapping for every possible vowel and consonant, the
`Transcription` will transparently use a distance method to find the closest
mapping available. If there is nothing close, it will transcribe the `Phone` as
an underscore (`_`) which we can deal with later.

```tut
import cats.data.NonEmptyList

println(phonology.pulmonics.map(p => NonEmptyList.one(p).transcribe).toList.mkString(", "))
println(phonology.vowels.map(v => NonEmptyList.one(v).transcribe).toList.mkString(", "))
```

## Get some monosyllables from the Phonology

The list of phones, while interesting, isn't very indicative of our language. We
need a list of rules for how they fit together into syllables. The `Phonology`
we generated includes these rules, so lets use it generate some syllables.

```tut
val syllableGen = phonology.syllable.map(_.transcribe)
syllableGen.pack(10).apply(rng).foreach(println)
```

## The Morphology

Pseudoglot's `Morphology` combines the generation of one or more syllables
together with rules for fusing them together into morphemes.

```tut
val morphology = Morphology(phonology)
val morphemeGen = morphology.morpheme.map(_.transcribe)
morphemeGen.pack(10).apply(rng).foreach(println)
```

## What's in a name

Finally, now that we can generate words of varying length, let's combine them to
make some names. We'll use the built-in `OnomasticRule` facility to do this.

```tut
val onomasticRules = OnomasticRule.dist(morphology).pack(5).apply(rng).distinct
val nameGen = for {
  rule <- Dist.oneOf(onomasticRules:_*)
  applied <- rule(morphology)
} yield applied.map(_.transcribe match {
  case xs if xs.length > 3 => xs.capitalize
  case xs => xs
}).mkString(" ").capitalize

nameGen.pack(20).apply(rng).foreach(println)
```
