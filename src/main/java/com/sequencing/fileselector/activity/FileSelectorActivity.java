package com.sequencing.fileselector.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

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
     * ToolTipView shows info
     */
    private ToolTipView  toolTipView;

    /**
     * Json with all files returned by server
     */
    private static String serverResponse;

    /**
     * Previous selected file in list, if there is
     */
    private static FileEntity previousSelectedFile;

    private static final String TAG = "FileSelector";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);

        serverResponse = getIntent().getStringExtra("serverResponse");
        previousSelectedFile = FileSelectHelper.getFileEntityByFileId(FileSelectHelper.getFileEntities(serverResponse), getIntent().getStringExtra("fileId"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.fs_colorAccent));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Init main tab host categories {My files and Sample files} that is placed in bottom
        tabHostCategory = (FragmentTabHost) findViewById(R.id.tabCategory);
        tabHostCategory.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        myFilesTabContent = getTabIndicator(tabHostCategory, "My files", R.drawable.my_files_icon_sequencing_com_gray);
        sampleFilesTabContent = getTabIndicator(tabHostCategory, "Sample files", R.drawable.sample_files_icon_sequencing_com_gray);

        tabHostCategory.addTab(tabHostCategory.newTabSpec(FileSelectHelper.ATTR_MY_FILES).setIndicator(myFilesTabContent), MyFileActivity.class, null);
        tabHostCategory.addTab(tabHostCategory.newTabSpec(FileSelectHelper.ATTR_SAMPLE).setIndicator(sampleFilesTabContent), SampleFileActivity.class, null);
        changeTabContent(myFilesTabContent, R.color.fs_colorAccent, R.drawable.my_files_icon_sequencing_com_color);

        tabHostCategory.setOnTabChangedListener(this);

        if(FileSelectHelper.isEmptyMyFiles(serverResponse)) {
            tabHostCategory.getTabWidget().getChildTabViewAt(1).performClick();
            tabHostCategory.getTabWidget().getChildTabViewAt(0).setEnabled(false);
        }

        if(getIntent().getStringExtra("tab").equals("sample_files")) {
            tabHostCategory.getTabWidget().getChildTabViewAt(1).performClick();
        }
    }

    /**
     * Handles main tabhost
     */
    @Override
    public void onTabChanged(String tabId) {
        hideOption(R.id.btnContinue);
        currentEntity = null;
        ListView fileList = null;

        switch (tabId){
            case FileSelectHelper.ATTR_MY_FILES:
                fileList = (ListView) findViewById(R.id.sampleFileList);
                changeTabContent(sampleFilesTabContent, R.color.tabUnselectedColor, R.drawable.sample_files_icon_sequencing_com_gray);
                changeTabContent(myFilesTabContent, R.color.fs_colorAccent, R.drawable.my_files_icon_sequencing_com_color);
                break;

            case FileSelectHelper.ATTR_SAMPLE:
                fileList = (ListView) findViewById(R.id.myFileList);
                changeTabContent(myFilesTabContent, R.color.tabUnselectedColor, R.drawable.my_files_icon_sequencing_com_gray);
                changeTabContent(sampleFilesTabContent, R.color.fs_colorAccent, R.drawable.sample_files_icon_sequencing_com_blue);
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
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltip);

        ToolTip toolTip = new ToolTip()
                .withText(R.string.empty_my_files)
                .withColor(Color.GRAY)
                .withTextColor(Color.WHITE)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        toolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, tabHostCategory.getTabWidget().getChildTabViewAt(1));
        toolTipView.setOnToolTipViewClickedListener(FileSelectorActivity.this);
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
            hideOption(R.id.btnContinue);
        else
            showOption(R.id.btnContinue);

        return true;
    }

    /**
     * Callback handles menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.btnContinue) {
            SQUIFileSelectHandler.getFileCallback().onFileSelected(currentEntity, this);
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
            this.toolTipView.remove();
            this.toolTipView = null;
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
        if(menu == null)
            return;
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentEntity", currentEntity);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentEntity = (FileEntity)savedInstanceState.getSerializable("currentEntity");
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

    public static FileEntity getPreviousSelectedFile() {
        return previousSelectedFile;
    }

    public static void setPreviousSelectedFile(FileEntity previousSelectedFile) {
        FileSelectorActivity.previousSelectedFile = previousSelectedFile;
    }
}
