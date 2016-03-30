package com.sequencing.fileselector.activity;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.sequencing.fileselector.FileEntity;
import com.sequencing.fileselector.R;
import com.sequencing.fileselector.core.SQUIFileSelectHandler;
import com.sequencing.fileselector.helper.FileSelectHelper;

/**
 * Activity shows all files form user account in Sequencing.com and groups
 * them by two categories My files and Sample files. Determines listeners of
 * menu items. Also class saves selected file entity and sends him to user handler.
 */
public class FileSelectorActivity extends AppCompatActivity implements FragmentTabHost.OnTabChangeListener, ToolTipView.OnToolTipViewClickedListener {

    /**
     * Main tabhost that contents my files tabhost and sample fiels tabhost.
     */
    private FragmentTabHost tabHostCategory;

    /**
     * Toolbar menu
     */
    private static Menu menu;

    /**
     * Selected file entity
     */
    private static FileEntity currentEntity;

    /**
     * View of my files tab
     */
    private View myFilesTabContent;

    /**
     * View of sample files tab
     */
    private View sampleFilesTabContent;

    /**
     * ToolTipView shows info about this app
     */
    private ToolTipView  infoToolTipView;

    /**
     * ToolTipView notify that user don't has own files
     */
    private ToolTipView  emptyMyFileToolTipView;

    /**
     * Json with all files returned by server
     */
    private static String serverResponse;

    private static final String TAG = "FileSelector";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);

        serverResponse = getIntent().getStringExtra("serverResponse");

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbar.setTitle("Select to file");
        toolbar.setTitleTextColor(getResources().getColor(R.color.fs_colorAccent));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // Init main tab host categories {My files and Sample files} that is placed in bottom
        tabHostCategory = (FragmentTabHost) findViewById(R.id.tabCategory);
        tabHostCategory.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        myFilesTabContent = getTabIndicator(tabHostCategory, "My files", R.mipmap.my_files_icon_sequencing_com_gray);
        sampleFilesTabContent = getTabIndicator(tabHostCategory, "Sample files", R.mipmap.sample_files_icon_sequencing_com_gray);

        tabHostCategory.addTab(tabHostCategory.newTabSpec(FileSelectHelper.ATTR_MY_FILES).setIndicator(myFilesTabContent), MyFileActivity.class, null);
        tabHostCategory.addTab(tabHostCategory.newTabSpec(FileSelectHelper.ATTR_SAMPLE).setIndicator(sampleFilesTabContent), SampleFileActivity.class, null);
        changeTabContent(myFilesTabContent, R.color.fs_colorAccent, R.mipmap.my_files_icon_sequencing_com_color);

        tabHostCategory.setOnTabChangedListener(this);

        if(FileSelectHelper.isEmptyMyFiles(serverResponse)) {
            tabHostCategory.getTabWidget().getChildTabViewAt(1).performClick();
            tabHostCategory.getTabWidget().getChildTabViewAt(0).setEnabled(false);
        }
    }

    /**
     * Handles main tabhost
     */
    @Override
    public void onTabChanged(String tabId) {
        hideOption(R.id.Continue);
        currentEntity = null;
        ListView fileList = null;

        switch (tabId){
            case FileSelectHelper.ATTR_MY_FILES:
                fileList = (ListView) findViewById(R.id.sampleFileList);
                changeTabContent(sampleFilesTabContent, R.color.tabUnselectedColor, R.mipmap.sample_files_icon_sequencing_com_gray);
                changeTabContent(myFilesTabContent, R.color.fs_colorAccent, R.mipmap.my_files_icon_sequencing_com_color);
                break;

            case FileSelectHelper.ATTR_SAMPLE:
                fileList = (ListView) findViewById(R.id.myFileList);
                changeTabContent(myFilesTabContent, R.color.tabUnselectedColor, R.mipmap.my_files_icon_sequencing_com_gray);
                changeTabContent(sampleFilesTabContent, R.color.fs_colorAccent, R.mipmap.sample_files_icon_sequencing_com_blue);
                break;
        }
        Log.d(TAG, "FileSelectorActivity: tab -> " + tabId);

        if (fileList != null)
            fileList.clearChoices();

        if (FileSelectHelper.isEmptyMyFiles(serverResponse))
            showNotificationEmptyMyFiles();

    }

    /**
     * Notify that user don't has own files
     */
    private void showNotificationEmptyMyFiles(){
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText(R.string.empty_my_files)
                .withTextColor(Color.WHITE)
                .withColor(Color.GRAY)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        emptyMyFileToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, tabHostCategory.getTabWidget().getChildTabViewAt(1));
        emptyMyFileToolTipView.setOnToolTipViewClickedListener(FileSelectorActivity.this);
    }

    /**
     * Changes color of tab text and icon
     * @param tabView view of tab
     * @param colorId color id
     */
    private void changeTabContent(View tabView, int colorId, int iconId){
        TextView title = (TextView) tabView.findViewById(R.id.tabTitle);
        title.setTextColor(ContextCompat.getColor(this, colorId));

        ImageView icon = (ImageView) tabView.findViewById(R.id.tabIcon);
        icon.setImageResource(iconId);
    }


    /**
     * Return view for filling tab content
     * @param tabHost tabHost of tab
     * @param name tab name
     * @param drawableId icon id of tab
     * @return view that is tab indicator
     */
    private View getTabIndicator(FragmentTabHost tabHost, String name, int drawableId)
    {
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);

        TextView title = (TextView) tabIndicator.findViewById(R.id.tabTitle);
        title.setText(name);

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tabIcon);
        icon.setImageResource(drawableId);

        tabIndicator.setBackgroundResource(R.drawable.tab_select_border);

        return tabIndicator;
    }

    /**
     * @return server response as json format
     */
    public static String getServerResponse(){
        return serverResponse;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_selector, menu);
        FileSelectorActivity.menu = menu;

        if (currentEntity == null)
            hideOption(R.id.Continue);
        else
            showOption(R.id.Continue);

        return true;
    }

    /**
     * Callback handles menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.Continue) {
            SQUIFileSelectHandler.getFileCallback().onFileSelected(currentEntity, this);
            return true;
        }
        else if(id == R.id.info) {
            if(infoToolTipView != null){
                infoToolTipView.remove();
                infoToolTipView = null;
                return true;
            }

            ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

            ToolTip toolTip = new ToolTip()
                    .withText(R.string.file_selector_info)
                    .withTextColor(Color.WHITE)
                    .withColor(Color.GRAY)
                    .withShadow()
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP);

            infoToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.info));
            infoToolTipView.setOnToolTipViewClickedListener(FileSelectorActivity.this);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Handles ToolTipViews
     * @param toolTipView pressed ToolTipView
     */
    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        if(infoToolTipView != null) {
            infoToolTipView.remove();
            infoToolTipView = null;
        } else if (emptyMyFileToolTipView != null) {
            emptyMyFileToolTipView.remove();
            emptyMyFileToolTipView = null;
        }
    }

    /**
     * Hide item in tool bar
     * @param id menu item id
     */
    public static void hideOption(int id)
    {
        if (menu != null) {
            MenuItem item = menu.findItem(id);
            item.setVisible(false);
        }
    }

    /**
     * Show item in tool bat
     * @param id menu item id
     */
    public static void showOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    /**
     * Save selected entity in list
     */
    public static void setCurrentEntity(FileEntity currentEntity)
    {
        FileSelectorActivity.currentEntity = currentEntity;
    }

    public static FileEntity getCurrentEntity()
    {
        return FileSelectorActivity.currentEntity;
    }
}
