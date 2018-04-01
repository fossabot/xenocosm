package pseudoglot

import cats.implicits._

/**
  * Lookups for the IPA
  *
  * It's important that there be a unique value for each key.
  */
object IPA {
  import data._

  /**
    * Handbook of the IPA (1999), Appendix 2, Table 5
    *
    * Common combining diacritics (Table 6):
    * * Retracted u0320 COMBINING MINUS SIGN BELOW
    * * Voiceless u0325 COMBINING RING BELOW
    * * Voiceless u030A COMBINING RING ABOVE
    * * Dental    u032A COMBINING BRIDGE BELOW
    */
  val pulmonics: Map[Pulmonic, String] = Map(
    Pulmonic(Voiceless, Bilabial, Plosive) ->                "\u0070",
    Pulmonic(Voiced, Bilabial, Plosive) ->                   "\u0062",
    Pulmonic(Voiceless, LabioDental, Plosive) ->             "\u0070\u032A",
    Pulmonic(Voiced, LabioDental, Plosive) ->                "\u0062\u032A",
    Pulmonic(Voiceless, Dental, Plosive) ->                  "\u0074\u032A",
    Pulmonic(Voiced, Dental, Plosive) ->                     "\u0064\u032A",
    Pulmonic(Voiceless, Alveolar, Plosive) ->                "\u0074",
    Pulmonic(Voiced, Alveolar, Plosive) ->                   "\u0064",
    Pulmonic(Voiceless, PostAlveolar, Plosive) ->            "\u0074\u0320",
    Pulmonic(Voiced, PostAlveolar, Plosive) ->               "\u0064\u0320",
    Pulmonic(Voiceless, Retroflex, Plosive) ->               "\u0288",
    Pulmonic(Voiced, Retroflex, Plosive) ->                  "\u0256",
    Pulmonic(Voiceless, Palatal, Plosive) ->                 "\u0063",
    Pulmonic(Voiced, Palatal, Plosive) ->                    "\u025F",
    Pulmonic(Voiceless, Velar, Plosive) ->                   "\u006B",
    Pulmonic(Voiced, Velar, Plosive) ->                      "\u0261",
    Pulmonic(Voiceless, Uvular, Plosive) ->                  "\u0071",
    Pulmonic(Voiced, Uvular, Plosive) ->                     "\u0262",
    Pulmonic(Voiceless, Pharyngeal, Plosive) ->              "\u02A1",
    Pulmonic(Voiceless, Glottal, Plosive) ->                 "\u0294",

    Pulmonic(Voiceless, Bilabial, Nasal) ->                  "\u006D\u0325",
    Pulmonic(Voiced, Bilabial, Nasal) ->                     "\u006D",
    Pulmonic(Voiceless, LabioDental, Nasal) ->               "\u0271\u0325",
    Pulmonic(Voiced, LabioDental, Nasal) ->                  "\u0271",
    Pulmonic(Voiceless, Dental, Nasal) ->                    "\u006E\u032A\u0325",
    Pulmonic(Voiced, Dental, Nasal) ->                       "\u006E\u032A",
    Pulmonic(Voiceless, Alveolar, Nasal) ->                  "\u006E\u0325",
    Pulmonic(Voiced, Alveolar, Nasal) ->                     "\u006E",
    Pulmonic(Voiceless, PostAlveolar, Nasal) ->              "\u006E\u0320\u0325",
    Pulmonic(Voiced, PostAlveolar, Nasal) ->                 "\u006E\u0320",
    Pulmonic(Voiceless, Retroflex, Nasal) ->                 "\u0273\u030A",
    Pulmonic(Voiced, Retroflex, Nasal) ->                    "\u0273",
    Pulmonic(Voiceless, Palatal, Nasal) ->                   "\u0272\u030A",
    Pulmonic(Voiced, Palatal, Nasal) ->                      "\u0272",
    Pulmonic(Voiceless, Velar, Nasal) ->                     "\u014B\u030A",
    Pulmonic(Voiced, Velar, Nasal) ->                        "\u014B",
    Pulmonic(Voiceless, Uvular, Nasal) ->                    "\u0274\u030A",
    Pulmonic(Voiced, Uvular, Nasal) ->                       "\u0274",

    Pulmonic(Voiceless, Bilabial, Trill) ->                  "\u0299\u030A",
    Pulmonic(Voiced, Bilabial, Trill) ->                     "\u0299",
    Pulmonic(Voiceless, LabioDental, Trill) ->               "\u0299\u032A\u030A",
    Pulmonic(Voiced, LabioDental, Trill) ->                  "\u0299\u032A",
    Pulmonic(Voiceless, Dental, Trill) ->                    "\u0072\u032A\u030A",
    Pulmonic(Voiced, Dental, Trill) ->                       "\u0072\u032A",
    Pulmonic(Voiceless, Alveolar, Trill) ->                  "\u0072\u030A",
    Pulmonic(Voiced, Alveolar, Trill) ->                     "\u0072",
    Pulmonic(Voiceless, PostAlveolar, Trill) ->              "\u0072\u0320\u030A",
    Pulmonic(Voiced, PostAlveolar, Trill) ->                 "\u0072\u0320",
    Pulmonic(Voiceless, Retroflex, Trill) ->                 "\u027D\u0361\u0072\u0325",
    Pulmonic(Voiced, Retroflex, Trill) ->                    "\u027D\u0361\u0072",
    Pulmonic(Voiceless, Palatal, Trill) ->                   "\u0272\u030A\u02B2",
    Pulmonic(Voiced, Palatal, Trill) ->                      "\u0272\u02B2",
    Pulmonic(Voiceless, Uvular, Trill) ->                    "\u0280\u0325",
    Pulmonic(Voiced, Uvular, Trill) ->                       "\u0280",
    Pulmonic(Voiceless, Pharyngeal, Trill) ->                "\u029C",
    Pulmonic(Voiced, Pharyngeal, Trill) ->                   "\u02A2",

    Pulmonic(Voiced, Bilabial, TapOrFlap) ->                 "\u2C71\u030A\u031F",
    Pulmonic(Voiceless, Bilabial, TapOrFlap) ->              "\u2C71\u031F",
    Pulmonic(Voiceless, LabioDental, TapOrFlap) ->           "\u2C71\u0325",
    Pulmonic(Voiced, LabioDental, TapOrFlap) ->              "\u2C71",
    Pulmonic(Voiceless, Dental, TapOrFlap) ->                "\u027E\u032A\u0325",
    Pulmonic(Voiced, Dental, TapOrFlap) ->                   "\u027E\u032A",
    Pulmonic(Voiceless, Alveolar, TapOrFlap) ->              "\u027E\u0325",
    Pulmonic(Voiced, Alveolar, TapOrFlap) ->                 "\u027E",
    Pulmonic(Voiceless, PostAlveolar, TapOrFlap) ->          "\u027E\u0320\u0325",
    Pulmonic(Voiced, PostAlveolar, TapOrFlap) ->             "\u027E\u0320",
    Pulmonic(Voiceless, Retroflex, TapOrFlap) ->             "\u027D\u0325",
    Pulmonic(Voiced, Retroflex, TapOrFlap) ->                "\u027D",
    Pulmonic(Voiceless, Palatal, TapOrFlap) ->               "\u028E\u030A\u032E",
    Pulmonic(Voiced, Palatal, TapOrFlap) ->                  "\u028E\u032E",
    Pulmonic(Voiceless, Uvular, TapOrFlap) ->                "\u0262\u0306\u0325",
    Pulmonic(Voiced, Uvular, TapOrFlap) ->                   "\u0262\u0306",
    Pulmonic(Voiceless, Pharyngeal, TapOrFlap) ->            "\u02A1\u030A\u032E",
    Pulmonic(Voiced, Pharyngeal, TapOrFlap) ->               "\u02A1\u032E",

    Pulmonic(Voiceless, Bilabial, Fricative) ->              "\u0278",
    Pulmonic(Voiced, Bilabial, Fricative) ->                 "\u03B2",
    Pulmonic(Voiceless, LabioDental, Fricative) ->           "\u0066",
    Pulmonic(Voiced, LabioDental, Fricative) ->              "\u0076",
    Pulmonic(Voiceless, Dental, Fricative) ->                "\u03B8",
    Pulmonic(Voiced, Dental, Fricative) ->                   "\u00F0",
    Pulmonic(Voiceless, Alveolar, Fricative) ->              "\u0073",
    Pulmonic(Voiced, Alveolar, Fricative) ->                 "\u007A",
    Pulmonic(Voiceless, PostAlveolar, Fricative) ->          "\u0283",
    Pulmonic(Voiced, PostAlveolar, Fricative) ->             "\u0292",
    Pulmonic(Voiceless, Retroflex, Fricative) ->             "\u0282",
    Pulmonic(Voiced, Retroflex, Fricative) ->                "\u0290",
    Pulmonic(Voiceless, Palatal, Fricative) ->               "\u00E7",
    Pulmonic(Voiced, Palatal, Fricative) ->                  "\u029D",
    Pulmonic(Voiceless, Velar, Fricative) ->                 "\u0078",
    Pulmonic(Voiced, Velar, Fricative) ->                    "\u0263",
    Pulmonic(Voiceless, Uvular, Fricative) ->                "\u03C7",
    Pulmonic(Voiced, Uvular, Fricative) ->                   "\u0281",
    Pulmonic(Voiceless, Pharyngeal, Fricative) ->            "\u0127",
    Pulmonic(Voiced, Pharyngeal, Fricative) ->               "\u0295",
    Pulmonic(Voiceless, Glottal, Fricative) ->               "\u0068",
    Pulmonic(Murmered, Glottal, Fricative) ->                "\u0266",

    Pulmonic(Voiceless, Dental, LateralFricative) ->         "\u026C\u032A\u0325",
    Pulmonic(Voiced, Dental, LateralFricative) ->            "\u026E\u032A",
    Pulmonic(Voiceless, Alveolar, LateralFricative) ->       "\u026C\u0325",
    Pulmonic(Voiced, Alveolar, LateralFricative) ->          "\u026E",
    Pulmonic(Voiceless, PostAlveolar, LateralFricative) ->   "\u026C\u0320\u0325",
    Pulmonic(Voiced, PostAlveolar, LateralFricative) ->      "\u026E\u0320",
    Pulmonic(Voiceless, Retroflex, LateralFricative) ->      "\uA78E",
    Pulmonic(Voiced, Retroflex, LateralFricative) ->         "\u026E\u0322",
    Pulmonic(Voiceless, Palatal, LateralFricative) ->        "\u028E\u02D4\u0325",
    Pulmonic(Voiced, Palatal, LateralFricative) ->           "\u028E\u02D4",
    Pulmonic(Voiceless, Velar, LateralFricative) ->          "\u029F\u031D\u030A",
    Pulmonic(Voiced, Velar, LateralFricative) ->             "\u029F\u031D",
    Pulmonic(Voiceless, Uvular, LateralFricative) ->         "\u0280\u0334\u030A",
    Pulmonic(Voiced, Uvular, LateralFricative) ->            "\u0280\u0334",

    Pulmonic(Voiceless, Bilabial, Approximant) ->            "\u03B2\u02D4\u030A",
    Pulmonic(Voiced, Bilabial, Approximant) ->               "\u03B2\u02D4",
    Pulmonic(Voiceless, LabioDental, Approximant) ->         "\u028B\u030A",
    Pulmonic(Voiced, LabioDental, Approximant) ->            "\u028B",
    Pulmonic(Voiceless, Dental, Approximant) ->              "\u0279\u032A\u030A",
    Pulmonic(Voiced, Dental, Approximant) ->                 "\u0279\u032A",
    Pulmonic(Voiceless, Alveolar, Approximant) ->            "\u0279\u030A",
    Pulmonic(Voiced, Alveolar, Approximant) ->               "\u0279",
    Pulmonic(Voiceless, PostAlveolar, Approximant) ->        "\u0279\u0320\u030A",
    Pulmonic(Voiced, PostAlveolar, Approximant) ->           "\u0279\u0320",
    Pulmonic(Voiceless, Retroflex, Approximant) ->           "\u027B\u030A",
    Pulmonic(Voiced, Retroflex, Approximant) ->              "\u027B",
    Pulmonic(Voiceless, Palatal, Approximant) ->             "\u006A\u030A",
    Pulmonic(Voiced, Palatal, Approximant) ->                "\u006A",
    Pulmonic(Voiceless, Velar, Approximant) ->               "\u0270\u030A",
    Pulmonic(Voiced, Velar, Approximant) ->                  "\u0270",
    Pulmonic(Voiceless, Uvular, Approximant) ->              "\u0281\u031E\u030A",
    Pulmonic(Voiced, Uvular, Approximant) ->                 "\u0281\u031E",
    Pulmonic(Voiceless, Pharyngeal, Approximant) ->          "\u0295\u031D\u030A",
    Pulmonic(Voiced, Pharyngeal, Approximant) ->             "\u0295\u031D",

    Pulmonic(Voiceless, Dental, LateralApproximant) ->       "\u006C\u032A\u0325",
    Pulmonic(Voiced, Dental, LateralApproximant) ->          "\u006C\u032A",
    Pulmonic(Voiceless, Alveolar, LateralApproximant) ->     "\u006C\u0325",
    Pulmonic(Voiced, Alveolar, LateralApproximant) ->        "\u006C",
    Pulmonic(Voiceless, PostAlveolar, LateralApproximant) -> "\u006C\u0320\u0325",
    Pulmonic(Voiced, PostAlveolar, LateralApproximant) ->    "\u006C\u0320",
    Pulmonic(Voiceless, Retroflex, LateralApproximant) ->    "\u026D\u030A",
    Pulmonic(Voiced, Retroflex, LateralApproximant) ->       "\u026D",
    Pulmonic(Voiceless, Palatal, LateralApproximant) ->      "\u028E\u0325",
    Pulmonic(Voiced, Palatal, LateralApproximant) ->         "\u028E",
    Pulmonic(Voiceless, Velar, LateralApproximant) ->        "\u029F\u030A",
    Pulmonic(Voiced, Velar, LateralApproximant) ->           "\u029F",
    Pulmonic(Voiceless, Uvular, LateralApproximant) ->       "\u029F\u0320\u030A",
    Pulmonic(Voiced, Uvular, LateralApproximant) ->          "\u029F\u0320"
  )

  /**
    * Handbook of the IPA (1999), Appendix 2, Table 5
    */
  val vowels: Map[Vowel, String] = Map(
    Vowel(Unrounded, Close, Front) ->                        "\u0069",
    Vowel(Rounded, Close, Front) ->                          "\u0079",
    Vowel(Unrounded, Close, Central) ->                      "\u0268",
    Vowel(Rounded, Close, Central) ->                        "\u0289",
    Vowel(Unrounded, Close, Back) ->                         "\u026F",
    Vowel(Rounded, Close, Back) ->                           "\u0075",

    Vowel(Unrounded, NearClose, NearFront) ->                "\u026A",
    Vowel(Rounded, NearClose, NearFront) ->                  "\u028F",
    Vowel(Unrounded, NearClose, Central) ->                  "\u026A\u0308",
    Vowel(Rounded, NearClose, Central) ->                    "\u028F\u0308",
    Vowel(Unrounded, NearClose, NearBack) ->                 "\u026F\u033D",
    Vowel(Rounded, NearClose, NearBack) ->                   "\u028A",

    Vowel(Unrounded, CloseMid, Front) ->                     "\u0065",
    Vowel(Rounded, CloseMid, Front) ->                       "\u00F8",
    Vowel(Unrounded, CloseMid, Central) ->                   "\u0258",
    Vowel(Rounded, CloseMid, Central) ->                     "\u0275",
    Vowel(Unrounded, CloseMid, Back) ->                      "\u0264",
    Vowel(Rounded, CloseMid, Back) ->                        "\u006F",

    Vowel(Unrounded, Mid, Front) ->                          "\u0065\u031E",
    Vowel(Rounded, Mid, Front) ->                            "\u00F8\u031E",
    Vowel(Unrounded, Mid, Central) ->                        "\u0259",
    Vowel(Rounded, Mid, Central) ->                          "\u0259\u0339",
    Vowel(Unrounded, Mid, Back) ->                           "\u0264\u031E",
    Vowel(Rounded, Mid, Back) ->                             "\u006F\u031E",

    Vowel(Unrounded, OpenMid, Front) ->                      "\u025B",
    Vowel(Rounded, OpenMid, Front) ->                        "\u0153",
    Vowel(Unrounded, OpenMid, Central) ->                    "\u025C",
    Vowel(Rounded, OpenMid, Central) ->                      "\u025E",
    Vowel(Unrounded, OpenMid, Back) ->                       "\u028C",
    Vowel(Rounded, OpenMid, Back) ->                         "\u0254",

    Vowel(Unrounded, NearOpen, Front) ->                     "\u00E6",
    Vowel(Rounded, NearOpen, Front) ->                       "\u0153\u031E",
    Vowel(Unrounded, NearOpen, Central) ->                   "\u0250",
    Vowel(Rounded, NearOpen, Central) ->                     "\u025C\u031E",

    Vowel(Unrounded, Open, Front) ->                         "\u0061",
    Vowel(Rounded, Open, Front) ->                           "\u0276",
    Vowel(Unrounded, Open, Central) ->                       "\u00E4",
    Vowel(Rounded, Open, Central) ->                         "\u0252\u0308",
    Vowel(Unrounded, Open, Back) ->                          "\u0251",
    Vowel(Rounded, Open, Back) ->                            "\u0252"
  )

  trait Instances {
    implicit val ipaHasTranscription:Transcription =
      Transcription(pulmonics) |+| Transcription(vowels)
  }
  object instances extends Instances
}
