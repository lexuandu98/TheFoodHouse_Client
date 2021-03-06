package thedark.example.com.thefoodhouse_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import thedark.example.com.thefoodhouse_client.Cart.CartActivity;
import thedark.example.com.thefoodhouse_client.Common.Common;
import thedark.example.com.thefoodhouse_client.Model.Category;
import thedark.example.com.thefoodhouse_client.Order.OrderStatusActivity;
import thedark.example.com.thefoodhouse_client.ViewHolder.MenuViewAdapter;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullName;
    TextView txtEmail;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    MenuViewAdapter menuViewAdapter;
    ArrayList<Category> categoryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        // Init Firebase:
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToCart = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(moveToCart);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for User:
        View headView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headView.findViewById(R.id.txtFullName);
        txtEmail = (TextView) headView.findViewById(R.id.txtEmail);
        txtFullName.setText(Common.currentUser.getName());
        txtEmail.setText(Common.currentUser.getEmail());

        //Load Menu:
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        menuViewAdapter = new MenuViewAdapter(categoryArrayList, Home.this);
        loadMenu();
    }

    private void loadMenu() {
        category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                    Category category = new Category();
                    String name = (String) messageSnapshot.child("Name").getValue();
                    String image = (String) messageSnapshot.child("Image").getValue();

                    category.setImage(image);
                    category.setName(name);
                    categoryArrayList.add(category);

                    recycler_menu.setAdapter(menuViewAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent moveToCart = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(moveToCart);
        } else if (id == R.id.nav_orders) {
            Intent moveToOrderView = new Intent(getApplicationContext(), OrderStatusActivity.class);
            startActivity(moveToOrderView);
        } else if (id == R.id.nav_log_out) {
            SharedPreferences sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("phone");
            editor.remove("password");
            editor.remove("logout");
            editor.apply();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
