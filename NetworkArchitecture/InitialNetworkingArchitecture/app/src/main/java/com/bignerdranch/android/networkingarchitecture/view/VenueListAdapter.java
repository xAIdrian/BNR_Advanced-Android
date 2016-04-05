package com.bignerdranch.android.networkingarchitecture.view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bignerdranch.android.networkingarchitecture.model.Venue;

import java.util.List;

public class VenueListAdapter extends RecyclerView.Adapter<VenueHolder> {
    private List<Venue> mVenueList;

    public VenueListAdapter(List<Venue> venueList) {
        mVenueList = venueList;
    }

    public void setVenueList(List<Venue> venueList) {
        mVenueList = venueList;
        notifyDataSetChanged();
    }

    @Override
    public VenueHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        VenueView venueView = new VenueView(viewGroup.getContext());
        return new VenueHolder(venueView);
    }

    @Override
    public void onBindViewHolder(VenueHolder venueHolder, int position) {
        venueHolder.bindVenue(mVenueList.get(position));
    }

    @Override
    public int getItemCount() {
        return mVenueList.size();
    }

    /**
     * this clears
     */
    public void clear() {
        mVenueList.clear();
        notifyDataSetChanged();
    }
}
