package upc.smtpra.perfeet.activity

import android.os.Bundle
import upc.smtpra.perfeet.AppBaseActivity
import upc.smtpra.perfeet.R
import upc.smtpra.perfeet.fragments.ProfileFragment
import upc.smtpra.perfeet.utils.extensions.addFragment
import kotlinx.android.synthetic.main.toolbar.*

class EditProfileActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setToolbar(toolbar)
        title=getString(R.string.lbl_edit_profile)
        addFragment(ProfileFragment(),R.id.profilecontainer)
    }
}
