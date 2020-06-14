package com.xep.thutiendien.activity

import android.app.SearchManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.xep.thutiendien.R


class MainActivity : AppCompatActivity() {

    var searchView: SearchView? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    var mSearchListener: ((query: String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(
                this@MainActivity,
                R.color.colorPrimary
            )
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_order,
                R.id.nav_clients,
                R.id.nav_order_paid,
                R.id.nav_order_cancel
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val imgHeader =
            findViewById<ImageView>(R.id.app_bar_main_img_header)
        val params: ViewGroup.LayoutParams = imgHeader.layoutParams
        params.height = 56
        params.width = 56 * 365 / 62
        imgHeader.layoutParams = params
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem: MenuItem? = menu.findItem(R.id.app_bar_search)

        val searchManager =
            this@MainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager


        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mSearchListener?.invoke(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //mSearchListener?.invoke(newText ?: "")
                return true
            }

        })
        searchView?.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                mSearchListener?.invoke(searchView?.query.toString())
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (searchView != null && searchView?.isIconified!!) {
            searchView?.onActionViewCollapsed()
        } else {

            super.onBackPressed();
        }

    }
}
