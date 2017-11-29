package com.trutek.looped.ui.recipient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Sandy on 12/1/2016.
 */

public class ContactProviderAdapter extends RecyclerView.Adapter<ContactProviderAdapter.MyViewHolder> {
    public List<ContactModel> filterContacts;
    private List<ContactModel> mainList;
    private OnActionListener<ContactModel> contactSelectedActionListeners;
    private OnActionListener<ContactModel> contactUnSelectedActionListeners;

    public ContactProviderAdapter(ArrayList<ContactModel> filterContacts, ArrayList<ContactModel> mainList,
                                  OnActionListener<ContactModel> contactSelectedActionListeners,
                                  OnActionListener<ContactModel> contactUnSelectedActionListeners) {
        this.filterContacts = filterContacts;
        this.mainList = mainList;
        this.contactSelectedActionListeners = contactSelectedActionListeners;
        this.contactUnSelectedActionListeners = contactUnSelectedActionListeners;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_invite_contact, parent, false);
        // Return a new holder instance
        MyViewHolder viewHolder = new MyViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ContactModel contactModel = filterContacts.get(position);
        holder.txtContactName.setText(contactModel.getName());
        holder.txtMobileNumber.setText(contactModel.getNumber());
        if(contactModel.isSelected){
            holder.imv_select_people.setImageResource(R.drawable.invite_people);
        }else{
            holder.imv_select_people.setImageResource(R.drawable.unselect_people);
        }
        holder.imv_select_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactModel.isSelected) {
                    contactUnSelectedActionListeners.notify(contactModel);
                    holder.imv_select_people.setImageResource(R.drawable.unselect_people);
                    contactModel.isSelected = false;
                } else {
                    contactSelectedActionListeners.notify(contactModel);
                    holder.imv_select_people.setImageResource(R.drawable.invite_people);
                    contactModel.isSelected = true;
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return filterContacts.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtContactName;
        TextView txtMobileNumber;
        ImageView imv_select_people;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtContactName = (TextView) itemView.findViewById(R.id.contact_name);
            txtMobileNumber = (TextView) itemView.findViewById(R.id.mobile_number);
            imv_select_people = (ImageView) itemView.findViewById(R.id.imageview_for_select_contact);

        }
    }

    public void filter(String charactersEnter) {
        charactersEnter = charactersEnter.toLowerCase(Locale.getDefault());
        filterContacts.clear();
        if (charactersEnter.length() == 0) {
            filterContacts.addAll(mainList);
        } else {
            for (ContactModel selectContactFromPhone : mainList) {
                if (selectContactFromPhone.getName().toLowerCase(Locale.getDefault())
                        .contains(charactersEnter)) {
                    filterContacts.add(selectContactFromPhone);
                }
            }
        }
        notifyDataSetChanged();
    }
}
