package upc.smtpra.perfeet.activity

import android.os.Bundle
import upc.smtpra.perfeet.AppBaseActivity
import upc.smtpra.perfeet.R
import upc.smtpra.perfeet.fragments.SearchFragment
import upc.smtpra.perfeet.utils.extensions.addFragment

class SearchActivity : AppBaseActivity() {

    private val mSearchFragment = SearchFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search2)
        addFragment(mSearchFragment, R.id.fragmentContainer)
    }

}