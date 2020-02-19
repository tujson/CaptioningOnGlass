package edu.gatech.cog.researcher.fragments

import android.os.Bundle
import androidx.navigation.NavArgs
import edu.gatech.cog.researcher.models.Message
import java.lang.IllegalArgumentException
import kotlin.Array
import kotlin.Suppress
import kotlin.jvm.JvmStatic

data class StudyFragmentArgs(
  val messages: Array<Message>
) : NavArgs {
  fun toBundle(): Bundle {
    val result = Bundle()
    result.putParcelableArray("messages", this.messages)
    return result
  }

  companion object {
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun fromBundle(bundle: Bundle): StudyFragmentArgs {
      bundle.setClassLoader(StudyFragmentArgs::class.java.classLoader)
      val __messages : Array<Message>?
      if (bundle.containsKey("messages")) {
        __messages = bundle.getParcelableArray("messages")?.map { it as Message }?.toTypedArray()
        if (__messages == null) {
          throw IllegalArgumentException("Argument \"messages\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"messages\" is missing and does not have an android:defaultValue")
      }
      return StudyFragmentArgs(__messages)
    }
  }
}
