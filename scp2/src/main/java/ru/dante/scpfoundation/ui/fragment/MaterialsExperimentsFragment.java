package ru.dante.scpfoundation.ui.fragment;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.mvp.contract.MaterialsExperimentsMvp;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class MaterialsExperimentsFragment
        extends BaseListArticlesWithSearchFragment<MaterialsExperimentsMvp.View, MaterialsExperimentsMvp.Presenter>
        implements MaterialsExperimentsMvp.View {

    public static final String TAG = MaterialsExperimentsFragment.class.getSimpleName();

    public static MaterialsExperimentsFragment newInstance() {
        return new MaterialsExperimentsFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (getUserVisibleHint()) {
            if (getActivity() instanceof ArticleFragment.ToolbarStateSetter) {
                ((ArticleFragment.ToolbarStateSetter) getActivity()).setTitle(getString(R.string.materials_experiments));
            }
        }
    }

    @Override
    public void resetOnScrollListener() {
        //we do not have paging
    }

    @Override
    protected boolean shouldUpdateThisListOnLaunch() {
        return false;
    }
}