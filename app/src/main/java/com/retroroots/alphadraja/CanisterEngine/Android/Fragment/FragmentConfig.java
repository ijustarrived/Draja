package com.retroroots.alphadraja.CanisterEngine.Android.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class FragmentConfig
{
    private FragmentTransaction fragmentTrans;

    public FragmentConfig() {}

    /**
     * Clears back stack and adds an initial item
     *
     * @param initialFragment
     */
    public Fragment ClearBackStack(FragmentManager fragmentManager, Fragment initialFragment)
    {
        //fragmentManager.popBackStack(null, fragmentManager.POP_BACK_STACK_INCLUSIVE);

        for(int i = 1; i <= fragmentManager.getBackStackEntryCount(); i++)
        {
            fragmentManager.popBackStack();
        }

        fragmentManager.executePendingTransactions();

        //fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

//        int firstStackEntry = fragmentManager.getBackStackEntryCount();
//
//         String name = fragmentManager.getBackStackEntryAt( 1 - firstStackEntry).getName();
//
//        Fragment fragment = fragmentManager.findFragmentByTag(name);

        return initialFragment;
    }

    /**
    Replace current fragment with a new fragment

    @param fragment Fragment replacement.

    @param shouldAddToBackStack flag that indicates if fragment should be added to back stack

    @param contentId Content view Id. Example: android.R.id.content

     @param fragmentManager Example getFragmentManager().

     @param fragmentTag Used to tag fragment. Could be blank.
     */
    public void ReplaceFragment(Fragment fragment,/* Bundle bundle,*/ int contentId, FragmentManager fragmentManager,
                                String fragmentTag, boolean shouldAddToBackStack)
    {
        fragmentTrans = fragmentManager.beginTransaction();

        if (shouldAddToBackStack)
        {
            fragmentTrans.addToBackStack(fragmentTag);
        }

        //fragment.setArguments(bundle);

        fragmentTrans.replace(contentId, fragment, fragmentTag);

        fragmentTrans.commit();
    }



    public void ReplaceFragment(Fragment fragment, Bundle bundle, int contentId, FragmentManager fragmentManager,
                                String fragmentTag, boolean shouldAddToBackStack)
    {
        fragmentTrans = fragmentManager.beginTransaction();

        if (shouldAddToBackStack)
        {
            fragmentTrans.addToBackStack(fragmentTag);
        }

        Bundle fragmentArguments = fragment.getArguments();

        if (fragmentArguments == null && !bundle.isEmpty())
            fragment.setArguments(bundle);

        fragmentTrans.replace(contentId, fragment, fragmentTag);

        fragmentTrans.commit();
    }


    /**Creates a new fragment

    @param fragment Fragment to be created.

    @param shouldAddToBackStack flag that indicates if fragment should be added to back stack

    @param contentId Content view Id. Example: android.R.id.content

     @param fragmentManager Example getFragmentManager().
     */
    public void CreateFragment(Fragment fragment, Bundle bundle, int contentId, FragmentManager fragmentManager, String fragmentTag, boolean shouldAddToBackStack)
    {
        fragmentTrans = fragmentManager.beginTransaction();

        if (shouldAddToBackStack)
            fragmentTrans.addToBackStack(fragmentTag);

        fragment.setArguments(bundle);

        fragmentTrans.add(contentId, fragment, fragmentTag);

        fragmentTrans.commit();
    }

    /**
     Tries to get retained fragment from manager and replaces if not empty. Else, returns false
     that way you can replace fragment manually.

     @param shouldAddToBackStack flag that indicates if fragment should be added to back stack

     @param contentId Content view Id. Example: android.R.id.content

     @param fragmentManager Example getFragmentManager().

     @return flag that indicates if fragment was found
     */
    public boolean ReloadFragment(FragmentManager fragmentManager, int contentId, boolean shouldAddToBackStack)
    {
        Fragment savedFragment = null;

        savedFragment = (Fragment) fragmentManager.findFragmentById(contentId);

        Bundle savedFragmentBundle = null;

        try
        {
            savedFragmentBundle = savedFragment.getArguments();
        }

        catch (Exception e)
        {
        }

        //Replace fragment if found. Done like this just in case you'd like to run
        // something if not found.
        if (savedFragment != null)
        {
            //Replace fragment without bundle
            if (savedFragmentBundle == null)
                ReplaceFragment(savedFragment, /*savedFragment.getArguments(), */ contentId, fragmentManager,
                        savedFragment.getTag(), shouldAddToBackStack);

            //Replace fragment with bundle
            else
                ReplaceFragment(savedFragment, savedFragmentBundle, contentId, fragmentManager,
                        savedFragment.getTag(), shouldAddToBackStack);

            return true;
        }

        else
            return false;

        //fragmentTrans.replace(contentId, )
    }

    public static void ClearBackStack(FragmentManager fragmentManager, boolean keepLastItem)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        int backStackCount = fragmentManager.getBackStackEntryCount();

            while (backStackCount >  (keepLastItem ? 1 : 0))
            {
                transaction.remove(fragmentManager.findFragmentById(
                        fragmentManager.getBackStackEntryAt(backStackCount - 1).getId()));

                backStackCount--;
            }

        transaction.commit();
    }

    /**
     * Pops stacks until told. Doesn't clean stack
     *
     * @param fragmentManager
     * @param fragmentTag flag that indicates stop
     * @throws Exception throws fragment not found if tag is not found
     */
    public static void PopStacks(FragmentManager fragmentManager, String fragmentTag) throws Exception
    {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (fragment == null)
            throw new Exception("Fragment not found");

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        FragmentManager.BackStackEntry backStackEntry;

        for( int i = fragmentManager.getBackStackEntryCount() - 1; i > 0; i--)
        {
            backStackEntry = fragmentManager.getBackStackEntryAt(i);

            if (backStackEntry.getName().equals(fragmentTag))
            {
                break;
            }

            else
            {
                Fragment currentFragment = fragmentManager.findFragmentByTag(backStackEntry.getName());

                //fragmentManager.popBackStack();

                transaction.remove(currentFragment);
            }
        }

        transaction.commit();
    }
}
