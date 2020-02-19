package edu.gatech.cog.researcher.fragments

import android.os.Bundle
import androidx.navigation.NavDirections
import edu.gatech.cog.researcher.R
import edu.gatech.cog.researcher.models.Message
import kotlin.Array
import kotlin.Int

class StudyFragmentDirections private constructor() {
  private data class ActionStudyFragmentToResultsFragment(
    val messages: Array<Message>
  ) : NavDirections {
    override fun getActionId(): Int = R.id.action_studyFragment_to_resultsFragment

    override fun getArguments(): Bundle {
      val result = Bundle()
      result.putParcelableArray("messages", this.messages)
      return result
    }
  }

  companion object {
    fun actionStudyFragmentToResultsFragment(messages: Array<Message>): NavDirections =
        ActionStudyFragmentToResultsFragment(messages)
  }
}
