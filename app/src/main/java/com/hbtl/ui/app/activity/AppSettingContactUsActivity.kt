package com.hbtl.ui.app.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.OnClickListener
import butterknife.BindView
import butterknife.ButterKnife
import com.hbtl.ekt.BaseActivity
import com.hbtl.ekt.R
import com.hbtl.service.NetworkType
import com.hbtl.ui.common.activity.CommonWebViewActivity
import com.hbtl.utils.CommonUtils

class AppSettingContactUsActivity : BaseActivity(), OnClickListener {

    @BindView(R.id.syOfficialWebLayout_LL) internal var syOfficialWebLayout_LL: View? = null
    @BindView(R.id.syTelLayout_LL) internal var syTelLayout_LL: View? = null

    @BindView(R.id.appNav_ToolBar) internal var appNav_ToolBar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_setting_contact_us_activity)
        ButterKnife.bind(this)

        // Inflate a menu to be displayed in the toolbar
        //appNav_ToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.my_account_space));
        appNav_ToolBar!!.title = "联系我们"

        setSupportActionBar(appNav_ToolBar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)//显示左侧回退按钮

        appNav_ToolBar!!.setNavigationOnClickListener { onBackPressed() }


        //        syOfficialWebLayout_LL = findViewById(R.id.officialWebLayout);
        syOfficialWebLayout_LL!!.setOnClickListener(this)
        //        syTelLayout_LL = findViewById(R.id.phoneLayout);
        syTelLayout_LL!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub
        when (v.id) {
            R.id.syOfficialWebLayout_LL -> gotoWebActivity("腾旅官网", resources.getString(R.string.official_web))
            R.id.syTelLayout_LL -> CommonUtils.call(this@AppSettingContactUsActivity, resources.getString(R.string.official_phone_number))
            else -> {
            }
        }
    }

    private fun gotoWebActivity(navTitle: String, website: String) {
        val intent = Intent(this@AppSettingContactUsActivity, CommonWebViewActivity::class.java)
        intent.putExtra("navTitle", navTitle)
        intent.putExtra("website", website)
        startActivity(intent)
    }

    override fun inNewIntent(intent: Intent) {
        // TODO ...
    }

    override fun onNwUpdateEvent(networkType: NetworkType) {
        // TODO ...
    }
}
