package com.csw.xposedclipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.csw.xposedclipboard.databinding.ActivityMainBinding;
import com.csw.xposedclipboard.util.MyTimeUtil;
import com.csw.xposedclipboard.util.MyToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static WeakReference<MainActivity> weakReference;

    private final String dataPath =
            "sdcard" + File.separator + "Android" + File.separator + "data" + File.separator
                    + "com.csw.xposedclipboard" + File.separator + "clipboard_data.dat";

    private final String settingPath =
            "sdcard" + File.separator + "Android" + File.separator + "data" + File.separator
                    + "com.csw.xposedclipboard" + File.separator + "setting.dat";


    private ActivityMainBinding binding;
    private BaseRecyclerViewAdapter<ClipBean> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =
                DataBindingUtil.setContentView(this, R.layout.activity_main);

        weakReference = new WeakReference<>(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout, binding.toolBar, R.string.open, R.string.close);
        drawerToggle.syncState();
        initDrawer();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<ClipBean> clipBeanList = getLocalClipData();
        if (clipBeanList != null)
            refreshClipBean(clipBeanList);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
                binding.drawerLayout.closeDrawer(Gravity.START);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initDrawer() {
        RecyclerView recyclerView = binding.include.recyclerViewDrawer;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DrawerAdapter adapter =
                new DrawerAdapter(this, BR.DrawerBean, R.layout.drawer_item);
        adapter.setListener(new DrawerAdapter.DrawerClickListener() {
            @Override
            public void onSwitchClickListener(DrawerBean bean, int pos, boolean isChecked) {
                bean.setIsSwitchChecked(isChecked);
                HashMap<String, String> settingMap = readConfig();
                if (pos == 0) {
                    settingMap.put("isRecord", String.valueOf(isChecked));
                } else if (pos == 1) {
                    settingMap.put("isToast", String.valueOf(isChecked));
                    if (isChecked) {
                        MyToast.show(MainActivity.this, null, "Toast提示开启");
                    }
                }
                writeConfig(settingMap);
            }
        });
        adapter.setItemClickListener(new BaseRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                if (pos == 2) {
                    Uri uri = Uri.parse("https://github.com/congshengwu/Xposed_Clipboard");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(int pos) {

            }

            @Override
            public void onItemRemove() {

            }
        });
        recyclerView.setAdapter(adapter);
        HashMap<String, String> settingMap = readConfig();
        DrawerBean bean1 = new DrawerBean();
        bean1.setName("记录剪切板使用数据");
        bean1.setIsNeedSwitch(true);
        bean1.setIsSwitchChecked(Boolean.valueOf(settingMap.get("isRecord")));
        bean1.setIcon(getDrawable(R.drawable.drawer_record_icon));
        DrawerBean bean2 = new DrawerBean();
        bean2.setName("剪切板使用时底部Toast提示");
        bean2.setIsNeedSwitch(true);
        bean2.setIsSwitchChecked(Boolean.valueOf(settingMap.get("isToast")));
        bean2.setIcon(getDrawable(R.drawable.drawer_toast_icon));
        DrawerBean bean3 = new DrawerBean();
        bean3.setName("GitHub开源地址");
        bean3.setIsNeedSwitch(false);
        bean3.setIcon(getDrawable(R.drawable.drawer_github_icon));
        adapter.addEnd(bean1);
        adapter.addEnd(bean2);
        adapter.addEnd(bean3);
    }

    private ArrayList<ClipBean> getLocalClipData() {
        try {
            File file = new File(dataPath);
            if (!file.exists()) {
                return null;
            }
            FileInputStream fileInput = new FileInputStream(dataPath);
            ObjectInputStream objInput = new ObjectInputStream(fileInput);
            Object object = objInput.readObject();
            if (object == null) {
                binding.textViewNothing.setVisibility(View.VISIBLE);
                return null;
            } else {
                ArrayList<ClipBean> clipBeanList = (ArrayList<ClipBean>) object;
                if (clipBeanList.size() == 0)
                    binding.textViewNothing.setVisibility(View.VISIBLE);
                else
                    binding.textViewNothing.setVisibility(View.GONE);
                return clipBeanList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.textViewNothing.setVisibility(View.VISIBLE);
        }
        return null;
    }

    private void initRecyclerView() {
        binding.recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseRecyclerViewAdapter<>(
                this, BR.ClipBean, R.layout.clip_item);
        adapter.setItemClickListener(new BaseRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }

            @Override
            public void onItemLongClick(final int pos) {
                View view = binding.recyclerViewMain.getChildAt(pos);
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(MainActivity.this, view);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.menu_copy, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_copy) {
                            ClipboardManager cm = (ClipboardManager) MainActivity.this.getSystemService(
                                    Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                String clipStr = adapter.getBeanList().get(pos).getClipText();
                                cm.setPrimaryClip(ClipData.newPlainText("text", clipStr));
                            }
                        }
                        return false;
                    }
                });
                popup.show();
            }

            @Override
            public void onItemRemove() {
                if (adapter.getBeanList().size() == 0)
                    binding.textViewNothing.setVisibility(View.VISIBLE);
                saveClipData(adapter.getBeanList());
            }
        });
        binding.recyclerViewMain.setAdapter(adapter);
        // 侧滑删除
        MyItemTouchHelperCallback callback = new MyItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(binding.recyclerViewMain);
    }

    private void refreshClipBean(ArrayList<ClipBean> beanList) {
        adapter.removeAll();
        for (ClipBean bean: beanList) {
            bean.setTime(MyTimeUtil.formatTime(bean.getTimeStamp()));
            adapter.addEnd(bean);
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageView_menu) {
            //创建弹出式菜单对象
            PopupMenu popup = new PopupMenu(this, view);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.menu_toolbar, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.menu_clearData) {
                        clearAllLocalClipData();
                    }
                    return false;
                }
            });
            //显示(这一行代码不要忘记了)
            popup.show();
        }
    }

    private void clearAllLocalClipData() {
        File file = new File(dataPath);
        if (file.exists())
            file.delete();
        if (adapter != null) {
            adapter.removeAll();
            binding.textViewNothing.setVisibility(View.VISIBLE);
        }
    }

    private void saveClipData(ArrayList<ClipBean> beanList) {
        try {
            File file = new File(dataPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            // 存到本地
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(beanList);
            objOut.flush();
            objOut.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeConfig(HashMap<String, String> settingMap) {
        try {
            File file = new File(settingPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            ObjectOutputStream objOutput = new ObjectOutputStream(fileOutput);
            objOutput.writeObject(settingMap);
            objOutput.flush();
            objOutput.close();
            fileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取本地存储的配置类,如果没有该文件类,则生成默认配置类,并将该类存到本地.
     * @return
     */
    private HashMap<String, String> readConfig() {
        try {
            File file = new File(settingPath);
            if (!file.exists()) {
                HashMap<String, String> settingMap = new HashMap<>();
                settingMap.put("isRecord", String.valueOf(true));
                settingMap.put("isToast", String.valueOf(true));
                writeConfig(settingMap);
                return settingMap;
            }
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objInput = new ObjectInputStream(fileInput);
            Object object = objInput.readObject();
            objInput.close();
            fileInput.close();
            if (object == null) {
                HashMap<String, String> settingMap = new HashMap<>();
                settingMap.put("isRecord", String.valueOf(true));
                settingMap.put("isToast", String.valueOf(true));
                return settingMap;
            } else {
                return (HashMap<String, String>) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> settingMap = new HashMap<>();
        settingMap.put("isRecord", String.valueOf(true));
        settingMap.put("isToast", String.valueOf(true));
        return settingMap;
    }

}
