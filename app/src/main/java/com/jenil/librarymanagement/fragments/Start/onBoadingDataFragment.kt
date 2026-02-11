package com.jenil.librarymanagement.fragments.Start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jenil.librarymanagement.R
import com.jenil.librarymanagement.databinding.FragmentOnBoadingDataBinding


class onBoadingDataFragment : Fragment() {

    private lateinit var binding : FragmentOnBoadingDataBinding

    companion object {

        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): onBoadingDataFragment {
            val fragment = onBoadingDataFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
     binding = FragmentOnBoadingDataBinding.inflate(layoutInflater)

        val position = arguments?.getInt(ARG_POSITION) ?: 0

        when (position) {
            0 -> {
                binding.ivImageView.setImageResource(R.drawable.onboarding)
                binding.tvTitle.text = getString(R.string.welcome_to_library_app)
                binding.tvDescription.text = getString(R.string.manage_your_library_with_ease)


            }
            1 -> {
                binding.ivImageView.setImageResource(R.drawable.image1)
                binding.tvTitle.text = getString(R.string.track_your_books)
                binding.tvDescription.text =
                    getString(R.string.keep_a_track_of_all_borrowed_books_and_due_dates)

            }
            2 -> {
                binding.ivImageView.setImageResource(R.drawable.image2)
                binding.tvTitle.text = getString(R.string.easy_returns)
                binding.tvDescription.text =
                    getString(R.string.easily_manage_book_returns_and_reminders)

            }
        }
        return binding.root
    }


}