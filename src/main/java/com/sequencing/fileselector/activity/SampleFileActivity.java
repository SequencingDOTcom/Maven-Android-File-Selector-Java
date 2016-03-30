package com.sequencing.fileselector.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.Set;

/**
 * Activity shows tab content with sample files
 */
public class SampleFileActivity extends Fragment implements AdapterView.OnItemClickListener{
    /**
     * Sample files tabhost
     */
    private TabHost tabHostSampleFile;

    /**
     * ListView in activity_sample_files
     */
    private ListView sampleFileList;

    /**
     * All entities from server response
     */
    private FileEntity[] allEntities;

    private static int currentTab = 0;

    private static final String TAG = "SampleFileActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_sample_files, container, false);

        sampleFileList = (ListView) v.findViewById(R.id.sampleFileList);
        sampleFileList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        sampleFileList.setOnItemClickListener(this);

        allEntities = FileSelectHelper.getFileEntities(FileSelectorActivity.getServerResponse());

        // Init tab host categories { Sample files}
        tabHostSampleFile = (TabHost) v.findViewById(R.id.tabHostSampleFiles);
        tabHostSampleFile.setup();

        initTabHost(tabHostSampleFile, getSampleFilesSubCategories());
        tabHostSampleFile.setOnTabChangedListener(new SampleFilesCategoriesListener());

        if(tabHostSampleFile.getCurrentTabTag().equals(FileSelectHelper.ATTR_SAMPLE_ALL)) {
            sampleFileList.setAdapter(getArrayAdapterForSampleFiles(allEntities, null));
        } else {
            sampleFileList.setAdapter(getArrayAdapterForSampleFiles(allEntities, tabHostSampleFile.getCurrentTabTag()));
        }

        FileEntity currentEntity = FileSelectorActivity.getCurrentEntity();
        tabHostSampleFile.setCurrentTab(currentTab);
        FileSelectorActivity.setCurrentEntity(currentEntity);

        return v;
    }

    /**
     * Save selected item to FileSelectorActivity field
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "itemClick: position = " + position + " tab: " + tabHostSampleFile.getCurrentTabTag());

        FileSelectorActivity.showOption(R.id.Continue);
        FileEntity currentEntity = null;

        if(tabHostSampleFile.getCurrentTabTag().equals(FileSelectHelper.ATTR_SAMPLE_ALL))
            currentEntity = FileSelectHelper.getAllSampleFileEntities(allEntities).get(position);
        else
            currentEntity = FileSelectHelper.getSampleFileEntitiesBySex(allEntities, tabHostSampleFile.getCurrentTabTag()).get(position);

        FileSelectorActivity.setCurrentEntity(currentEntity);
    }

    /**
     * Handler of tabhost of sample files
     */
    private class SampleFilesCategoriesListener implements TabHost.OnTabChangeListener {

        @Override
        public void onTabChanged(String tabId) {
            switch (tabId){
                case FileSelectHelper.ATTR_SAMPLE_ALL:
                    sampleFileList.setAdapter(getArrayAdapterForSampleFiles(allEntities, null));
                    break;
                case FileSelectHelper.ATTR_SAMPLE_MALE:
                case FileSelectHelper.ATTR_SAMPLE_FEMALE:
                    sampleFileList.setAdapter(getArrayAdapterForSampleFiles(allEntities, tabId));
                    break;
            }
            FileSelectorActivity.setCurrentEntity(null);
            FileSelectorActivity.hideOption(R.id.Continue);
            currentTab = tabHostSampleFile.getCurrentTab();
            Log.i(TAG, "SampleFilesCategoriesListener -> " + tabId);
        }
    }

    /**
     *
     * @param entities all entities from server response
     * @param sex variant sex
     * @return ArrayAdapter for current listview
     */
    private ArrayAdapter<SpannableString> getArrayAdapterForSampleFiles(FileEntity[] entities, String sex){
        if (sex == null)
            return new ArrayAdapter<SpannableString>(getContext(), R.layout.activity_list_item_1, FileSelectHelper.getAllSampleFileEntitiesName(entities));
        else
            return new ArrayAdapter<SpannableString>(getContext(), R.layout.activity_list_item_1, FileSelectHelper.getSampleFileEntitiesNameBySex(entities, sex));
    }

    /**
     * @return TabSpec array for sample files tab host
     */
    private TabHost.TabSpec[] getSampleFilesSubCategories(){
        Set<String> sampleFilesSubCategoriesSet = FileSelectHelper.getSampleFilesSubCategories();

        TabHost.TabSpec[] sampleFilesSubCategories = new TabHost.TabSpec[sampleFilesSubCategoriesSet.size()];

        Iterator<String> categoriesIter = sampleFilesSubCategoriesSet.iterator();
        for(int i = 0; i < sampleFilesSubCategories.length; i++) {
            String subCategoryName = categoriesIter.next();

            sampleFilesSubCategories[i] = tabHostSampleFile.newTabSpec(subCategoryName);
            sampleFilesSubCategories[i].setIndicator(FileSelectHelper.sampleCategoriesMap.get(subCategoryName));
            sampleFilesSubCategories[i].setContent(R.id.sampleFileList);
        }

        return sampleFilesSubCategories;
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
