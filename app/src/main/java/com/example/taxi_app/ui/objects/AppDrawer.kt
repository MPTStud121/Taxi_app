package com.example.taxi_app.ui.objects

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.taxi_app.MainActivity
import com.example.taxi_app.R
import com.example.taxi_app.ui.screens.HelpFragment
import com.example.taxi_app.ui.screens.PayMethodFragment
import com.example.taxi_app.ui.screens.ProfileFragment
import com.example.taxi_app.utilites.*
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem

class AppDrawer(var mainActivity: MainActivity,var toolbar: Toolbar) {

    private lateinit var drawer: Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var header: AccountHeader
    private lateinit var currentProfile: ProfileDrawerItem

    //Create and init drawer
    fun create() {
        initFirebase()
        createHeader()
        createDrawer()
        drawerLayout = drawer.drawerLayout
    }

    //Disable drawer
    fun disableDrawer() {
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.setNavigationOnClickListener {
            mainActivity.supportFragmentManager.popBackStack()
        }
    }

    //Enable drawer
    fun enableDrawer() {
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        toolbar.setNavigationOnClickListener {
            drawer.openDrawer()
        }
    }

    //Create header
    private fun createHeader() {
        currentProfile = ProfileDrawerItem()
            .withIcon(R.mipmap.ic_launcher)
            .withEmail(PHONE)
        header = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                currentProfile
            ).build()
    }

    //Create drawer
    private fun createDrawer() {
        drawer = DrawerBuilder()
            .withActivity(mainActivity)
            .withAccountHeader(header)
            .withSliderBackgroundColorRes(R.color.white)
            .withToolbar(toolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100)
                    .withIconTintingEnabled(true)
                    .withName("Профиль")
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withName("Способ оплаты")
                    .withSelectable(false),
                PrimaryDrawerItem().withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withName("Служба поддержки")
                    .withSelectable(false),
            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when (position) {
                        1 -> mainActivity.replaceFragment(ProfileFragment())
                        2 -> mainActivity.replaceFragment(PayMethodFragment())
                        3 -> mainActivity.replaceFragment(HelpFragment())
                    }
                    return false
                }
            }).build()
    }
}