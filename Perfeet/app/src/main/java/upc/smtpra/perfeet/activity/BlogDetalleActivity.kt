package upc.smtpra.perfeet.activity

import android.os.Bundle
import upc.smtpra.perfeet.AppBaseActivity
import upc.smtpra.perfeet.R
import upc.smtpra.perfeet.models.Blog
import upc.smtpra.perfeet.utils.Constants.KeyIntent.DATA
import upc.smtpra.perfeet.utils.extensions.loadImageFromUrl
import kotlinx.android.synthetic.main.activity_blog_detalle.*
import kotlinx.android.synthetic.main.toolbar.*

class BlogDetalleActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_detalle)

        setToolbar(toolbar)

        val blog = intent.getSerializableExtra(DATA) as Blog
        title = blog.title

        ivProduct.loadImageFromUrl(blog.image!!)
        tvTitle.text = blog.title
        tvPublishDate.text = blog.publish_date
        tvDescription.text = blog.description
    }
}
