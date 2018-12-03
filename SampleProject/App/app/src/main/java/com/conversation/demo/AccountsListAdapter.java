package com.conversation.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nanorep.nanoengine.Account;

import java.util.List;

import com.conversation.demo.R;


public class AccountsListAdapter extends RecyclerView.Adapter<AccountsListAdapter.ViewHolder> {

    public interface AccountsListListener {
        void onUserAccountSelected(Account account);
    }

    private final List<Account> accounts;
    private final AccountsListListener accountsListListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mAccountTextView;
        public TextView mApiKeyView;
        public TextView mKnowledgeBaseTextView;
        public TextView mServerTextView;

        public ViewHolder(View v) {
            super(v);
            mAccountTextView = (TextView) v.findViewById(R.id.list_item_account);
            mApiKeyView = (TextView) v.findViewById(R.id.list_item_apikey);
            mKnowledgeBaseTextView = (TextView) v.findViewById(R.id.list_item_knowledgebase);
            mServerTextView = (TextView) v.findViewById(R.id.list_item_server);
            v.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            accountsListListener.onUserAccountSelected(accounts.get(getAdapterPosition()));
        }
    }

    public AccountsListAdapter(List<Account> accounts, AccountsListListener accountsListListener) {
        this.accounts = accounts;
        this.accountsListListener = accountsListListener;
    }

    @Override
    public AccountsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mAccountTextView.setText(accounts.get(position).toString());
        /*holder.mApiKeyView.setText(accounts.get(position).getApiKey());
        holder.mKnowledgeBaseTextView.setText(accounts.get(position).getKnowledgeBase());
        holder.mServerTextView.setText(accounts.get(position).getDomain());*/
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}