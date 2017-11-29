package com.trutek.looped.ui.profile.create.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.msas.common.contracts.OnActionListener;

import java.util.List;
import java.util.Locale;

/**
 * Created by dell on 12/10/16.
 */
public class InviteAdapterRecycler extends RecyclerView.Adapter<InviteAdapterRecycler.MyViewHolder> {


    public List<ContactModel> filterContacts;
    private List<ContactModel> mainList;
    private OnActionListener<ContactModel> contactSelectedActionListeners;
    private OnActionListener<ContactModel> contactUnSelectedActionListeners;

    public InviteAdapterRecycler(List<ContactModel> mainList,
                                 List<ContactModel> filterContacts,
                                 OnActionListener<ContactModel> contactSelectedActionListeners,
                                 OnActionListener<ContactModel> contactUnSelectedActionListeners) {
        this.contactSelectedActionListeners = contactSelectedActionListeners;
        this.contactUnSelectedActionListeners = contactUnSelectedActionListeners;
        this.mainList = mainList;
        this.filterContacts = filterContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final ContactModel contactModel = filterContacts.get(position);
        holder.txt_contact_name.setText(contactModel.getName());
        holder.txt_contact_number.setText(contactModel.getNumber());

        holder.imv_select_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactModel.isSelected){
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
    public long getItemId(int position){
      return position;
    }

    @Override
    public int getItemCount() {
        return filterContacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_contact_name,txt_contact_number;
        ImageView imv_select_people;

        public MyViewHolder(View view) {
            super(view);
            txt_contact_name=(TextView)view.findViewById(R.id.contact_name);
            txt_contact_number=(TextView)view.findViewById(R.id.mobile_number);
            imv_select_people=(ImageView)view.findViewById(R.id.imageview_for_select_contact);

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
