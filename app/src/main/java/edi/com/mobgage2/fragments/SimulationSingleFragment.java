package edi.com.mobgage2.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edi.com.mobgage2.R;
import edi.com.mobgage2.data.Proposal;
import edi.com.mobgage2.data.SimulationDetails;
import edi.com.mobgage2.data.SimulationRow;
import edi.com.mobgage2.managers.DataManager;


public class SimulationSingleFragment extends Fragment {

    private static final String PROPOSAL_ID = "proposalId";

    private String proposalId;
    private RecyclerView recyclerView;
    private SimulationSingleAdapter adapter;
    private TextView offer;

    public SimulationSingleFragment() {
        // Required empty public constructor
    }

    public static SimulationSingleFragment newInstance(String proposalId) {
        SimulationSingleFragment fragment = new SimulationSingleFragment();

        Bundle args = new Bundle();
        args.putString(PROPOSAL_ID, proposalId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            proposalId = getArguments().getString(PROPOSAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simulation_single, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.simulation_single_recyclerView);
        offer = (TextView)view.findViewById(R.id.simulation_single_offer);

        adapter =  new SimulationSingleAdapter(proposalId);
        recyclerView.setAdapter(adapter);

        Proposal proposal = DataManager.getInstance().getProposalByProposalID(proposalId);
        String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;
        String rowTitle = bankName + " - " + getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(proposal.proposalID));
        offer.setText(rowTitle);

        return view;
    }

    //    getProposalPositionByID
    private class SimulationSingleAdapter extends RecyclerView.Adapter<SimulationSingleAdapter.CustomViewHolder>{

        private Proposal proposal;
        private String proposalId;

        private double big;
        private double fig;
        private double ig;
        private double cap;

        private List<SimulationRow> rows;

        public SimulationSingleAdapter(String proposalId) {
            this.proposalId = proposalId;
            this.proposal = DataManager.getInstance().getProposalByProposalID(this.proposalId);

            SimulationDetails simulationDetails = DataManager.getInstance().getSimulationDetails();
            big = simulationDetails.getBankIsraelAnnualGrowth();
            fig = simulationDetails.getFixedInterestAnnualGrowth();
            ig = simulationDetails.getIndexAnnualGrowth();
            cap = simulationDetails.getCapInterest();

            rows = new ArrayList<>();

        }

        @Override
        public SimulationSingleAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simulation_single_row, null);

            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimulationSingleAdapter.CustomViewHolder holder, int position) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);

            year = year + (month + position) / 12;
            month = ((month + position) % 12) + 1;

            holder.date.setText(month + "/" + year);

            if(rows.size() > 0){
                SimulationRow row = rows.get(position);
                int INm = 0;
                int YE = 0;
                int LP0 = 0;
                int payment = ((INm/12*(1+INm/12)^YE*12)/((1+INm/12)^YE-1))*LP0;
            }
        }

        @Override
        public int getItemCount() {
            return proposal.getMaxYears() * 12; // months
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            private TextView interest;
            private TextView left;
            private TextView month_return;
            private TextView date;
            private View parent;



            public CustomViewHolder(View view) {
                super(view);
                this.parent = view;
                this.interest = (TextView) view.findViewById(R.id.single_row_interest);
                this.left = (TextView) view.findViewById(R.id.single_row_left);
                this.month_return = (TextView) view.findViewById(R.id.single_row_month_return);
                this.date = (TextView) view.findViewById(R.id.single_row_date);
///
            }
        }
    }


}
