package com.nullvoid.blooddonation.beans;

import org.parceler.Parcel;

/**
 * Created by sanath on 19/06/17.
 */

@Parcel
public class SelectionDonor extends Donor {
    private boolean isSelected = false;
    private Donor donor;

    public SelectionDonor() {
    }

    public SelectionDonor(boolean isSelected, Donor donor){
        this.isSelected = isSelected;
        this.donor = donor;
    }

    public SelectionDonor(Donor donor) {
        this.donor = donor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }
}
