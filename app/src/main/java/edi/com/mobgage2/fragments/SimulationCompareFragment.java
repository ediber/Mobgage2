package edi.com.mobgage2.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import edi.com.mobgage2.R;
import edi.com.mobgage2.activities.MobgageMainActivity;
import edi.com.mobgage2.data.Proposal;
import edi.com.mobgage2.managers.DataManager;
import edi.com.mobgage2.utils.NumberUtils;


public class SimulationCompareFragment extends Fragment {


    private RecyclerView recyclerView;
    private SimulationCompareAdapter adapter;

    public SimulationCompareFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SimulationCompareFragment newInstance() {
        SimulationCompareFragment fragment = new SimulationCompareFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simulation_compare, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new SimulationCompareAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void moveNext(String proposalID) {
        ((MobgageMainActivity) (getActivity())).showScreen(MobgageMainActivity.SCREEN_USER_SIMULATION_SINGLE, true, proposalID);
    }


    private class SimulationCompareAdapter extends RecyclerView.Adapter<SimulationCompareAdapter.CustomViewHolder> {

        private List<Proposal> proposals;
        private Context context;

        public SimulationCompareAdapter(Context context) {
            this.context = context;
            this.proposals = DataManager.getInstance().getProposalsListByOrder();
            Collections.sort(proposals, new TotalRepaymentComparator());
//            Collections.sort(proposals, new NumberComparator());
        }


        @Override
        public SimulationCompareAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulation_compare_row, parent, false);



            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimulationCompareAdapter.CustomViewHolder holder, int position) {
            final Proposal proposal = proposals.get(position);

            String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;
            String rowTitle =  context.getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(proposal.proposalID)) + " - " + bankName;

            if(proposal.isRecommendation == 1){
                rowTitle = rowTitle + " \n (" + getResources().getString(R.string.proposal_recommendation) + ")" ;
            }

            holder.name.setText(rowTitle);

            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveNext(proposal.getProposalID());
                }
            });

            holder.overall.setText(NumberUtils.doubleToMoney(proposal.getTotalRepayment()) + "");
            holder.monthlyReturn.setText(NumberUtils.doubleToMoney(proposal.getMonthRepayment()) + "");
        }

        @Override
        public int getItemCount() {
            return proposals.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            private View parent;
            private TextView overall;
            private TextView monthlyReturn;
            private TextView name;


            public CustomViewHolder(View view) {
                super(view);
                this.parent = view;
                this.overall = (TextView) view.findViewById(R.id.compare_row_overall);
                this.monthlyReturn = (TextView) view.findViewById(R.id.compare_row_monthly_return);
                this.name = (TextView) view.findViewById(R.id.compare_row_name);

            }
        }

        private class TotalRepaymentComparator implements java.util.Comparator<Proposal> {

            @Override
            public int compare(Proposal prop1, Proposal prop2) {
                int ans;
                if (prop1.getTotalRepayment() > prop2.getTotalRepayment()) {
                    ans = 1;
                } else if (prop1.getTotalRepayment() == prop2.getTotalRepayment()) {
                    ans = 0;
                } else {
                    ans = -1;
                }

                return ans;
            }
        }
    }


    private class NumberComparator implements java.util.Comparator<Proposal> {

        @Override
        public int compare(Proposal lhs, Proposal rhs) {
            return ((Integer)lhs.proposalNum).compareTo(rhs.proposalNum);
        }
    }
}
