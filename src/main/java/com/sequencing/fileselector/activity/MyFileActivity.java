package com.sequencing.fileselector.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import com.sequencing.fileselector.FileEntity;
import com.sequencing.fileselector.R;
import com.sequencing.fileselector.helper.FileSelectHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Activity shows tab content with own files
 */
public class MyFileActivity extends Fragment implements AdapterView.OnItemClickListener {
    /**
     * My files tabhost
     */
    private TabHost tabHostMyFile;

    /**
     * ListView in activity_my_files
     */
    private ListView myFileList;

    /**
     * All entities from server response
     */
    private FileEntity[] allEntities;

    private static int currentTab = 0;

    private static final String TAG = "MyFileActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_my_files, container, false);

        myFileList = (ListView) v.findViewById(R.id.myFileList);
        myFileList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        myFileList.setOnItemClickListener(this);

        allEntities = FileSelectHelper.getFileEntities(FileSelectorActivity.getServerResponse());

        // Init tab host categories {My files}
        tabHostMyFile = (TabHost) v.findViewById(R.id.tabHostMyFiles);
        tabHostMyFile.setup();

        initTabHost(tabHostMyFile, getMyFilesCategories(allEntities));
        tabHostMyFile.setOnTabChangedListener(new MyFilesCategoriesListener());

        myFileList.setAdapter(getArrayAdapterForMyFiles(allEntities, tabHostMyFile.getCurrentTabTag()));

        FileEntity currentEntity = FileSelectorActivity.getCurrentEntity();
        tabHostMyFile.setCurrentTab(currentTab);
        FileSelectorActivity.setCurrentEntity(currentEntity);

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        FileEntity previousFile = FileSelectorActivity.getPreviousSelectedFile();
        if(previousFile != null
                && !previousFile.getFileCategory().equals(FileSelectHelper.ATTR_SAMPLE)){
            List<String> sampleCategories = FileSelectHelper.getMyFilesSubCategories(allEntities);

            int tabIndex = sampleCategories.indexOf(previousFile.getFileCategory());
            tabHostMyFile.getTabWidget().getChildTabViewAt(tabIndex).performClick();

            List<FileEntity> allSampleFileEntities = FileSelectHelper.getMyFileEntitiesByCategory(allEntities, previousFile.getFileCategory());
            int fileIndex = allSampleFileEntities.indexOf(previousFile);

            myFileList.performItemClick(
                    myFileList.getAdapter().getView(fileIndex, null, null),
                    fileIndex,
                    myFileList.getAdapter().getItemId(fileIndex));

            myFileList.setSelection(fileIndex);
        }
    }

    /**
     * Save selected item to FileSelectorActivity field
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "itemClick: position = " + position + " tab: " + tabHostMyFile.getCurrentTabTag());

        FileSelectorActivity.showOption(R.id.btnContinue);
        FileEntity currentEntity = FileSelectHelper.getMyFileEntitiesByCategory(allEntities, tabHostMyFile.getCurrentTabTag()).get(position);
        FileSelectorActivity.setCurrentEntity(currentEntity);
    }


    /**
     * Handler of tabhost of my files
     */
    private class MyFilesCategoriesListener implements TabHost.OnTabChangeListener {

        @Override
        public void onTabChanged(String tabId) {
            switch (tabId){
                case FileSelectHelper.ATTR_MY_FILES_UPLOADED:
                case FileSelectHelper.ATTR_MY_FILES_SHARED:
                case FileSelectHelper.ATTR_MY_FILES_ALTRUIST:
                case FileSelectHelper.ATTR_MY_FILES_FROMAPPS:
                    myFileList.setAdapter(getArrayAdapterForMyFiles(allEntities, tabId));
                    FileSelectorActivity.setCurrentEntity(null);
                    FileSelectorActivity.hideOption(R.id.btnContinue);
                    currentTab = tabHostMyFile.getCurrentTab();
                    break;
            }
            Log.i(TAG, "MyFilesCategoriesListener -> " + tabId);
        }
    }

    /**
     * @param entities all entities from server response
     * @param category my files category
     * @return ArrayAdapter for current listview
     */
    private ArrayAdapter<String> getArrayAdapterForMyFiles(FileEntity[] entities, String category)
    {
        return  new ArrayAdapter<String>(getContext(), R.layout.activity_list_item_1, FileSelectHelper.getMyFileEntitiesNameByCategory(entities, category));
    }

    /**
     * @return TabSpec array for my files tab host
     */
    private TabHost.TabSpec[] getMyFilesCategories(FileEntity[] allEntities){
        List<String> myFilesSubCategoriesList = FileSelectHelper.getMyFilesSubCategories(allEntities);

        TabHost.TabSpec[] myFilesSubCategories = new TabHost.TabSpec[myFilesSubCategoriesList.size()];

        Iterator<String> categoriesIter = myFilesSubCategoriesList.iterator();
        for(int i = 0; i < myFilesSubCategories.length; i++) {
            String subCategoryName = categoriesIter.next();

            myFilesSubCategories[i] = tabHostMyFile.newTabSpec(subCategoryName);
            myFilesSubCategories[i].setIndicator(FileSelectHelper.myFilesCategoriesMap.get(subCategoryName));
            myFilesSubCategories[i].setContent(R.id.myFileList);
        }

        return myFilesSubCategories;
    }

    /**
     * Set all tabs to defined tabhost
     * @param tabHost tabhost
     * @param tabspecs array of tabs
     */
    private void initTabHost(TabHost tabHost, TabHost.TabSpec []tabspecs){
        for(TabHost.TabSpec tabSpec: tabspecs) {
            tabHost.addTab(tabSpec);
        }
    }
}
