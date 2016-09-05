package edi.com.mobgage2.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import edi.com.mobgage2.data.Route;
import edi.com.mobgage2.data.SimulationDetails;
import edi.com.mobgage2.data.SimulationRow;
import edi.com.mobgage2.managers.DataManager;
import edi.com.mobgage2.utils.NumberUtils;


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

        recyclerView = (RecyclerView) view.findViewById(R.id.simulation_single_recyclerView);
        offer = (TextView) view.findViewById(R.id.simulation_single_offer);

        adapter = new SimulationSingleAdapter(proposalId);
        recyclerView.setAdapter(adapter);

        Proposal proposal = DataManager.getInstance().getProposalByProposalID(proposalId);
        String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;
        String rowTitle = bankName + " - " + getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(proposal.proposalID));
        offer.setText(rowTitle);

        return view;
    }

    //    getProposalPositionByID
    private class SimulationSingleAdapter extends RecyclerView.Adapter<SimulationSingleAdapter.CustomViewHolder> {

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

            SimulationRow simulationRow = new SimulationRow();


//            String[] routesKindsNames = getResources().getStringArray(R.array.routes_kinds_names);
            for (int i = 0; i < proposal.getRoutes().size(); i++) {
                calculateRoute(i, position, simulationRow);
            }

            if(rows.size() > position){
                rows.remove(position);
            }
            rows.add(position, simulationRow);

            holder.month_return.setText(NumberUtils.doubleToMoney(simulationRow.getPayment()));
            holder.interest.setText(NumberUtils.doubleToMoney(simulationRow.getInterest()));
            holder.left.setText(NumberUtils.doubleToMoney(simulationRow.getLoanBalance()));

//                DataManager.getInstance().getRouteKindByID(rout.routeKind);

        }

        @NonNull
        private void calculateRoute(int routeIndex, int adapterPosition, SimulationRow simulationRow) {
            Route rout = proposal.getRoutes().get(routeIndex);

            switch (rout.getRouteKind()) {
                case 0: // ribit prime


                    ribitPrimeCalculate(adapterPosition, rout, simulationRow);

                    break;
                case 1: // ribit kvua tsmuda lamadad

                    break;
                case 2: // ribit kvua lo tsmuda

                    break;
                case 3: // ribit mishtana tsmuda lamadad

                    break;
                case 4: // ribit mishtana lo tsmuda

                    break;
                case 5: // ribit dolarit

                    break;


            }


        }

        private void ribitPrimeCalculate(int adapterPosition, Route rout, SimulationRow simulationRow) {
            double INm0;
            double LP0;
            if (adapterPosition == 0) {
                INm0 = (DataManager.PRIME_INTEREST + rout.getInterest()) /12;
                LP0 = rout.getLoanAmount();
            } else {  // previous row
                INm0 = rows.get(adapterPosition - 1).getAnnualLoanInterestPerMonth();
                LP0 = rows.get(adapterPosition - 1).getLoanBalance();
            }


            int YE = proposal.getYears();
//            ((INm2*(1+INm2)^(YE*12))/((1+INm2)^(YE*12)-1))*LP1
            double payment = ((INm0 * Math.pow(1 + INm0, YE * 12) ) / (Math.pow(1 + INm0, YE*12) - 1)) * LP0;
            double loanBalance = LP0 - payment;


            double annualLoanInterestPerMonth = Math.min((big / 12 * (adapterPosition) + INm0) / 12, cap / 12);
            double interest = annualLoanInterestPerMonth / 12 * loanBalance;
//                     double principal;

            incrementSimulationRow(simulationRow, payment, loanBalance, annualLoanInterestPerMonth, interest);

        }

        private void incrementSimulationRow(SimulationRow simulationRow, double payment, double loanBalance, double annualLoanInterestPerMonth, double interest) {
            simulationRow.addAnnualLoanInterestPerMonth(annualLoanInterestPerMonth);
            simulationRow.addInterest(interest);
            simulationRow.addLoanBalance(loanBalance);
            simulationRow.addPayment(payment);
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
