package br.com.halyson.ensharp.service;

import br.com.halyson.ensharp.interfaces.home.HomeService;
import br.com.halyson.ensharp.repository.HomeRepositoryDiskImpl;

/**
 * Created by Halyson on 20/01/15.
 */
public class HomeServiceImpl implements HomeService {
    private static final String TAG = HomeServiceImpl.class.getSimpleName();
    private HomeRepositoryDiskImpl mHomeRepositoryDisk;

    public HomeServiceImpl() {
        mHomeRepositoryDisk = new HomeRepositoryDiskImpl();
    }

    @Override
    public void recoverTitleTabs() {
        mHomeRepositoryDisk.recoverTitleTabs();
    }

    @Override
    public void recoverColorTabs() {
        mHomeRepositoryDisk.recoverColorsTabs();
    }

    @Override
    public void onDestroy() {
        mHomeRepositoryDisk.onDestroy();
        mHomeRepositoryDisk = null;
    }
}
