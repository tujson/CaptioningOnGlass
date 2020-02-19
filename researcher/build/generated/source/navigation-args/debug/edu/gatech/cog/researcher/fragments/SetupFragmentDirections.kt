package edu.gatech.cog.researcher.fragments

import android.os.Bundle
import androidx.navigation.NavDirections
import edu.gatech.cog.researcher.R
import edu.gatech.cog.researcher.models.Message
import kotlin.Array
import kotlin.Int

class SetupFragmentDirections private constructor() {
  private data class ActionSetupFragmentToStudyFragment(
    val messages: Array<Message>
  ) : NavDirections {
    override fun getActionId(): Int = R.id.action_setupFragment_to_studyFragment

    override fun getArguments(): Bundle {
      val result = Bundle()
      result.putParcelableArray("messages", this.messages)
      return result
    }
  }

  companion object {
    fun actionSetupFragmentToStudyFragment(messages: Array<Message>): NavDirections =
        ActionSetupFragmentToStudyFragment(messages)
  }
}
