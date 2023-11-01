package com.example.photos.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.photos.R
import com.example.photos.databinding.ActivityMainBinding
import com.example.photos.ui.view_model.PhotosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val viewModel by viewModels<PhotosViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.navHostFragment)
        setMenu()
        setMenuVisible()
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun setupActionBar(navController: NavController, appBarConfig: AppBarConfiguration) {
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun setupNavigationMenu(navController: NavController) {
        binding.navMenu.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    private fun setupClickListener() {
        binding.navMenu.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = !menuItem.isChecked
            when (menuItem.itemId) {
                R.id.nav_photos -> {
                    if (navController.currentDestination?.id == R.id.mapFragment) {
                        navController.navigate(R.id.action_mapFragment_to_photosFragment)
                    }
                }

                R.id.nav_map -> {
                    if (navController.currentDestination?.id == R.id.photosFragment) {
                        navController.navigate(R.id.action_photosFragment_to_mapFragment)
                    }
                }
            }
            closeDrawer()
            true
        }
    }

    private fun setMenu() {
        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.photosFragment, R.id.mapFragment),
            binding.drawerLayout
        )

        setupActionBar(navController, appBarConfiguration)
        setupNavigationMenu(navController)

        setupClickListener()
        checkCurrentFragment()
        viewModel.setMenuInitialized(true)
    }

    fun checkCurrentFragment() {
        val currentFragment = navController.currentDestination?.id
        if (currentFragment == R.id.mapFragment) {
            binding.navMenu.setCheckedItem(R.id.nav_map)
        } else if (currentFragment == R.id.photosFragment) {
            binding.navMenu.setCheckedItem(R.id.nav_photos)
        }
    }

    private fun setMenuVisible() {
        with(binding) {
            toolbar.isVisible = true
            navMenu.isVisible = true
        }
    }

    override fun onDestroy() {
        viewModel.setMenuInitialized(false)
        super.onDestroy()
    }
}