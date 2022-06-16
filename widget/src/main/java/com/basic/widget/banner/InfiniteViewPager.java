package com.basic.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class InfiniteViewPager extends ViewPager {
    //数据放大的倍率，粗略的估算一下按目前的参数配置，
    // 如果adapter有3个item，3秒播一次，播完所有的数据要4500（SCALE_FACTOR * 3 * 3/ 2）秒，
    // 这样已经满足基本使用需求了
    private static final int                SCALE_FACTOR = 1000;
    private              PageChangeListener mListener;

    public InfiniteViewPager(@NonNull Context context) {
        this(context, null);
    }

    public InfiniteViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mListener = new PageChangeListener();
        super.addOnPageChangeListener(mListener);
    }

    /**
     Adapter的类型必须是{@link InfiniteAdapter}，否则{@link InfiniteViewPager} 无法正常工作

     @param adapter
     */
    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        setAdapter(adapter, 0);
    }

    public void setAdapter(@Nullable PagerAdapter adapter, int offset) {
        if (adapter instanceof InfiniteAdapter) {
            int realCount = ((InfiniteAdapter) adapter).getRealCount();
            mListener.setRealCount(realCount);
            super.setAdapter(adapter);
            //把第一页设定到中间，这样就既能向前滑又能向后滑
            setCurrentItem(realCount * SCALE_FACTOR / 2 + offset);
        } else {
            throw new IllegalArgumentException("Adapter must extend InfiniteAdapter");
        }
    }

    public void setCurrentItemByRealSize(int item) {
        if (getAdapter() != null) {
            int realCount = ((InfiniteAdapter) getAdapter()).getRealCount();
            setCurrentItem(realCount * SCALE_FACTOR / 2);
        }
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (item >= getAdapter().getCount() - 1) {
            super.setCurrentItem(getRealCount() * SCALE_FACTOR / 2);
        } else {
            super.setCurrentItem(item, smoothScroll);
        }
    }

    public int getRealCount() {
        InfiniteAdapter adapter = (InfiniteAdapter) getAdapter();
        if (adapter == null) {
            return 0;
        }
        return adapter.getRealCount();
    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        mListener.addPageChangeListener(listener);
    }

    @Override
    public void removeOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        mListener.removePageChangeListener(listener);
    }

    private static class PageChangeListener implements OnPageChangeListener {
        private int                       realCount;
        private Set<OnPageChangeListener> mPageChangeListeners;

        public PageChangeListener() {
            mPageChangeListeners = new HashSet<>();
        }

        public void setRealCount(int realCount) {
            this.realCount = realCount;
        }

        void addPageChangeListener(OnPageChangeListener listener) {
            mPageChangeListeners.add(listener);
        }

        void removePageChangeListener(OnPageChangeListener listener) {
            mPageChangeListeners.remove(listener);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            position = calculateVirtualPosition(position);
            for (OnPageChangeListener listener : mPageChangeListeners) {
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            position = calculateVirtualPosition(position);
            for (OnPageChangeListener listener : mPageChangeListeners) {
                listener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            for (OnPageChangeListener listener : mPageChangeListeners) {
                listener.onPageScrollStateChanged(state);
            }
        }

        private int calculateVirtualPosition(int position) {
            if (realCount != 0) {
                return position % realCount;
            }
            return 0;
        }
    }

    public static abstract class InfiniteAdapter<T> extends PagerAdapter {
        private SparseArray<Stack<Object>> mCache;
        private List<T>                    mList;
        private boolean                    infinite;

        public InfiniteAdapter(List<T> list) {
            this(list, true);
        }

        public InfiniteAdapter(List<T> list, boolean infinite) {
            this.infinite = infinite;
            mList = list;
            mCache = new SparseArray<>();
        }

        @Override
        public int getCount() {
            if (isEmpty(mList)) {
                return 0;
            }
            //这里数值不要设置得太大，不然在setCurrentItem的时候会anr
            if (infinite) {
                return mList.size() * SCALE_FACTOR;
            }
            return mList.size();
        }

        public int getRealCount() {
            if (isEmpty(mList)) {
                return 0;
            }
            return mList.size();
        }

        public boolean isEmpty(List<?> list) {
            return list == null || list.size() == 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @NonNull
        @Override
        public final Object instantiateItem(@NonNull ViewGroup container, int position) {
            return instantiateItem2(container, getFromCache(position), getRealPosition(position),
                    getData(position));
        }

        /**
         构造要显示的内容，并将之放入到container中，子类不要做缓存，这里已经缓存过了

         @param container
         @param cache     在{@link #destroyItem(ViewGroup, int, Object)}的时候会将Object
         对象缓存下来，这样就不用每次都创建新的对象
         @param position
         @param data      当前item对应的数据

         @return
         */
        public abstract Object instantiateItem2(@NonNull ViewGroup container,
                                                @Nullable Object cache,
                                                int position,
                                                T data);

        @Override
        public final void destroyItem(@NonNull ViewGroup container,
                                      int position,
                                      @NonNull Object object) {
            putIntoCache(position, object);
            destroyItem2(container, getRealPosition(position), object);
        }

        /**
         子类重写这个函数

         @param container
         @param position
         @param data
         */
        public abstract void destroyItem2(@NonNull ViewGroup container,
                                          int position,
                                          @NonNull Object data);

        @Override
        public final void setPrimaryItem(@NonNull ViewGroup container,
                                         int position,
                                         @NonNull Object object) {
            setPrimaryItem2(container, getRealPosition(position), object);
        }

        /**
         子类重写这个函数

         @param container
         @param position
         @param object
         */
        public void setPrimaryItem2(@NonNull ViewGroup container,
                                    int position,
                                    @NonNull Object object) {

        }

        @Nullable
        @Override
        public final CharSequence getPageTitle(int position) {
            return getPageTitle2(getRealPosition(position));
        }

        public CharSequence getPageTitle2(int position) {
            return null;
        }

        @Override
        public final float getPageWidth(int position) {
            return getPageWidth2(getRealPosition(position));
        }

        public float getPageWidth2(int position) {
            return 1.f;
        }

        public T getData(int position) {
            return mList.get(getRealPosition(position));
        }

        private int getRealPosition(int position) {
            if (getRealCount() == 0) {
                return -1;
            }
            return position % getRealCount();
        }

        private void putIntoCache(int position, Object o) {
            int realPosition = getRealPosition(position);
            Stack<Object> objects = mCache.get(realPosition);
            if (objects == null) {
                objects = new Stack<>();
                mCache.put(realPosition, objects);
            }
            objects.push(o);
        }

        private Object getFromCache(int position) {
            int realPosition = getRealPosition(position);
            Stack<Object> objects = mCache.get(realPosition);
            if (objects == null || objects.empty()) {
                return null;
            }
            return objects.pop();
        }

        public void refresh(List<T> list) {
            mList = list;
            notifyDataSetChanged();
        }
    }

    /**
     View版本的{@link InfiniteAdapter}
     */
    public static abstract class ViewAdapter<T> extends InfiniteAdapter<T> {

        public ViewAdapter(List<T> list) {
            super(list);
        }

        public ViewAdapter(List<T> list, boolean infinite) {
            super(list, infinite);
        }

        @Override
        public Object instantiateItem2(@NonNull ViewGroup container,
                                       @Nullable Object cache,
                                       int position,
                                       T data) {
            View view = (View) cache;
            if (view == null) {
                view = createView(container.getContext(), position);
            }
            initiateView(view, data, position);
            container.addView(view);
            return view;
        }

        @CallSuper
        @Override
        public final void destroyItem2(@NonNull ViewGroup container,
                                       int position,
                                       @NonNull Object data) {
            View view = (View) data;
            container.removeView(view);
            recycleView(view);
        }

        //创建View
        protected abstract View createView(Context context, int position);

        //初始化View
        protected abstract void initiateView(View view, T data, int position);

        /**
         如果你的View中存在内存占用比较大的（比如ImageView的大图片），
         请手动回收这部分的内存图片，不然由于Adapter会缓存View，
         View中引用的占用内存资源的部分会一直得不到回收

         @param view
         */
        protected void recycleView(View view) {
            //手动清空Bitmap引用，不然这里会一直持有该Bitmap得不到回收
            //如果是其他类型的View记得释放当前持有的占用内存较大的部分(不仅限于Bitmap)
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(null);
            }
        }
    }

    /**
     Fragment版本的{@link InfiniteAdapter}
     */
    public abstract static class FragmentAdapter<T> extends InfiniteAdapter<T> {
        private FragmentManager     mFragmentManager;
        private FragmentTransaction mCurTransaction;

        public FragmentAdapter(List<T> list) {
            super(list);
        }

        public FragmentAdapter(List<T> list, boolean infinite) {
            super(list, infinite);
        }

        @Override
        public Object instantiateItem2(@NonNull ViewGroup container,
                                       @Nullable Object cache,
                                       int position,
                                       T data) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            Fragment fragment = (Fragment) cache;
            if (fragment == null) {
                fragment = createFragment(position);
            }
            initiateFragment(fragment, data);

            mCurTransaction.add(container.getId(), fragment);

            return fragment;
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
            }
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            Fragment fragment = (Fragment) object;
            return view == fragment.getView();
        }

        @Override
        public void destroyItem2(@NonNull ViewGroup container, int position, @NonNull Object data) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            /**
             * 这里与{@link android.support.v4.app.FragmentPagerAdapter}的实现不一样是因为在{@link InfiniteAdapter}
             * 中已经实现了缓存，这里就不使用{@link FragmentManager}来进行管理了，以免相同的操作做两次，而且本身与传统的
             * {@link ViewPager}实现不一致每一个item可能存在多个相同的Fragment，如果通过{@link FragmentManager}来管理
             * 会更加麻烦
             */
            mCurTransaction.remove((Fragment) data);
        }

        protected abstract Fragment createFragment(int position);

        /**
         如果需要将数据绑定到Fragment，通过这个函数来实现

         @param fragment
         @param data
         */
        protected void initiateFragment(Fragment fragment, T data) {
            //empty
        }
    }
}
