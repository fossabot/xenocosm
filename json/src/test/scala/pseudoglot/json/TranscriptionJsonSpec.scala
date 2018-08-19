package pseudoglot.json

import io.circe.syntax._
import pseudoglot.IPA
import pseudoglot.data.Transcription

class TranscriptionJsonSpec extends xenocosm.test.XenocosmFunSuite {
  import transcription._

  test("Transcription.json.isomorphism") {
    val xscript = IPA.instances.ipaHasTranscription
    withClue(xscript.asJson) {
      xscript.asJson.as[Transcription] shouldBe Right(xscript)
    }
  }
}
