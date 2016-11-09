package edi.com.mobgage2.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edi.com.mobgage2.R;
import edi.com.mobgage2.activities.MobgageMainActivity;
import edi.com.mobgage2.data.Proposal;
import edi.com.mobgage2.data.Route;
import edi.com.mobgage2.data.SimulationDetails;
import edi.com.mobgage2.data.SimulationRow;
import edi.com.mobgage2.managers.DataManager;
import edi.com.mobgage2.utils.NumberUtils;

import static edi.com.mobgage2.managers.DataManager.INTEREST_GROWTH_PER_CYCLE;


public class SimulationCompareFragment extends Fragment {


    private RecyclerView recyclerView;
    private SimulationCompareAdapter adapter;

// totla payment
    private List<SimulationRow> totalRows;
    private List<SimulationRow> fixedRateSpitzerLinkedRows;
    private List<SimulationRow> fixedRateFixedPrincipalLinkedRows;
    private List<SimulationRow> primeLoanSpitzerRows;
    private List<SimulationRow> primeLoanFixedPrincipalRows;
    private List<SimulationRow> fixedRateFixedPrincipalRows;
    private List<SimulationRow> fixedRateSpitzerRows;
    private List<SimulationRow> nonFixedRateSpitzerRows;
    private List<SimulationRow> nonFixedRateSpitzerLinkedRows;

    private double big;
    private double fig;
    private double ig;
    private double cap;
    private ArrayList<Proposal> proposals;

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

        proposals = DataManager.getInstance().getProposalsListByOrder();

        for (Proposal proposal:proposals) {
            initTotalPaymentData();
            double totalBalance = calculateTotalPayment(proposal);
            double totalInterestPercentage = (totalBalance / proposal.getMortgageAmount() - 1) * 100;
            proposal.setTotalPayment((int)totalBalance);
            proposal.setTotalPercentage((int)totalInterestPercentage);
        }
        Collections.sort(proposals, new PercentageComparator());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new SimulationCompareAdapter(getActivity().getApplicationContext(), proposals);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void moveNext(String proposalID) {
        ((MobgageMainActivity) (getActivity())).showScreen(MobgageMainActivity.SCREEN_USER_SIMULATION_SINGLE, true, proposalID);
    }


    private class SimulationCompareAdapter extends RecyclerView.Adapter<SimulationCompareAdapter.CustomViewHolder> {

        private List<Proposal> proposals;
        private Context context;



        private String proposalId;
        private Proposal proposal;

        public SimulationCompareAdapter(Context context, ArrayList<Proposal> proposals) {
            this.context = context;
            this.proposals = proposals;
//            Collections.sort(proposals, new TotalRepaymentComparator());
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
            proposal = proposals.get(position);
            proposalId = proposal.proposalID;

            String bankName = DataManager.getInstance().getBankByID(proposal.bank).bankName;
            String rowTitle =  context.getResources().getString(R.string.list_proposal_num) + " " + (DataManager.getInstance().getProposalPositionByID(proposalId)) + " - " + bankName;



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

            holder.initialMortgage.setText(NumberUtils.doubleToMoney(proposal.getMortgageAmount()) + "");
            holder.totalPercentage.setText(proposal.getTotalPercentage() + "%");
        }

        @Override
        public int getItemCount() {
            return proposals.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            private View parent;
            private TextView initialMortgage;
            private TextView totalPercentage;
            private TextView name;


            public CustomViewHolder(View view) {
                super(view);
                this.parent = view;
                this.initialMortgage = (TextView) view.findViewById(R.id.compare_row_initial_mortgage);
                this.totalPercentage = (TextView) view.findViewById(R.id.compare_row_total_percentage);
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

    private class PercentageComparator implements java.util.Comparator<Proposal> {

        @Override
        public int compare(Proposal lhs, Proposal rhs) {
            return ((Integer)lhs.getTotalPercentage()).compareTo(rhs.getTotalPercentage());
        }
    }



    private void initTotalPaymentData() {
        SimulationDetails simulationDetails = DataManager.getInstance().getSimulationDetails();
        big = simulationDetails.getBankIsraelAnnualGrowth() / 100;
        fig = simulationDetails.getFixedInterestAnnualGrowth();
        ig = (Math.pow(1 + simulationDetails.getIndexAnnualGrowth() / 100, 1.0 / 12.0) - 1);
        cap = simulationDetails.getCapInterest() / 100;

        totalRows = new ArrayList<>();
        fixedRateSpitzerLinkedRows = new ArrayList<>();
        fixedRateFixedPrincipalLinkedRows = new ArrayList<>();
        primeLoanSpitzerRows = new ArrayList<>();
        primeLoanFixedPrincipalRows = new ArrayList<>();
        fixedRateFixedPrincipalRows = new ArrayList<>();
        fixedRateSpitzerRows = new ArrayList<>();
        nonFixedRateSpitzerRows = new ArrayList<>();
        nonFixedRateSpitzerLinkedRows = new ArrayList<>();
    }

    private double calculateTotalPayment(Proposal proposal) {
        double totalBalance = 0;
//        Proposal proposal = DataManager.getInstance().getProposalByProposalID(this.proposalId);

        int months = proposal.getMaxYears() * 12;
        for(int position = 0; position < months; position++){
            SimulationRow simulationTotalRow = new SimulationRow();

            for (int i = 0; i < proposal.getRoutes().size(); i++) {
                calculateRoute(i, position, simulationTotalRow, proposal);
            }

            if (totalRows.size() > position) {
                totalRows.remove(position);
            }
            totalRows.add(position, simulationTotalRow);
        }

        for (SimulationRow row:totalRows) {
            totalBalance = totalBalance + row.getPayment();
        }

        return totalBalance;
    }

    @NonNull
    private void calculateRoute(int routeIndex, int adapterPosition, SimulationRow simulationRow, Proposal proposal) {
        Route rout = proposal.getRoutes().get(routeIndex);
        if (rout.getReturnMethod() == 1) { // spitzer

            switch (rout.getRouteKind()) {
                case 0: // ribit prime -- prime loan spitzer
                    primeLoanSpitzer(adapterPosition, rout, simulationRow);
                    break;
                case 1: // ribit kvua tsmuda lamadad -- fixed rate spitzer linked
                    fixedRateSpitzerLinked(adapterPosition, rout, simulationRow);
                    break;
                case 2: // ribit kvua lo tsmuda -- fixed rate spitzer
                    fixedRateSpitzer(adapterPosition, rout, simulationRow);
                    break;

                case 3:
                    nonFixedRateSpitzer(adapterPosition, rout, simulationRow);
                    break;

                case 4:
                    nonFixedRateSpitzerLinked(adapterPosition, rout, simulationRow);
                    break;
            }

        } else if (rout.getReturnMethod() == 0) { // keren shava
            switch (rout.getRouteKind()) {
                case 0: // prime loan - fixed prinsipal
                    primeLoanFixedPrincipal(adapterPosition, rout, simulationRow);
                    break;
                case 1: // fixed rate - fixed principal linked
                    fixedRateFixedPrincipalLinked(adapterPosition, rout, simulationRow);
                    break;
                case 2: // fixed rate - fixed principal
                    fixedRateFixedPrincipal(adapterPosition, rout, simulationRow);
                    break;

            }
        }
    }

    private void fixedRateSpitzerLinked(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double payment;

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            INm = rout.getInterest() / (100 * 12);  //100 for precentage   annualLoanInterestPerMonth

            int YE = rout.getYears();

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                payment = ((INm * Math.pow(1 + INm, YE * 12)) / (Math.pow(1 + INm, YE * 12) - 1)) * LPPrev;
            } else {  // previous row
                LPPrev = fixedRateSpitzerLinkedRows.get(adapterPosition - 1).getLoanBalance();
                //  Pmt*(1+Ig)^(n-1)
                double firstPayment = fixedRateSpitzerLinkedRows.get(0).getPayment();
                payment = firstPayment * Math.pow(1 + ig, adapterPosition);
            }

            double interest = INm * LPPrev;
            double principal = payment - interest;
            double loanBalance = (LPPrev - principal) * (1 + ig);

            updateSimulationSpecificRow(fixedRateSpitzerLinkedRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }


    private void fixedRateFixedPrincipalLinked(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double payment;
        double principal;
        double pp;

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            INm = rout.getInterest() / (100 * 12);  //100 for precentage   annualLoanInterestPerMonth
            pp = rout.getLoanAmount() / (rout.getYears() * 12);

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                principal = pp;
            } else {  // previous row
                LPPrev = fixedRateFixedPrincipalLinkedRows.get(adapterPosition - 1).getLoanBalance();
                principal = pp * Math.pow(1 + ig, adapterPosition);
            }

            double interest = INm * LPPrev;
            payment = principal + interest;
            double loanBalance = (LPPrev - principal) * (1 + ig);

            updateSimulationSpecificRow(fixedRateFixedPrincipalLinkedRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }

    private void primeLoanSpitzer(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double INmInitial; // Annual loan interest per month

        if (adapterPosition < rout.getYears() * 12) { // within loan months

            INmInitial = (DataManager.PRIME_INTEREST - rout.getInterest()) / 100;  //100 for precentage   annualLoanInterestPerMonth

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                INm = INmInitial / 12;
            } else {  // previous row
                LPPrev = primeLoanSpitzerRows.get(adapterPosition - 1).getLoanBalance();
                INm = Math.min((big / 12 * (adapterPosition) + INmInitial) / 12, cap / 12);
            }

            int YE = rout.getYears();

            double payment = ((INm * Math.pow(1 + INm, YE * 12 - adapterPosition)) / (Math.pow(1 + INm, YE * 12 - adapterPosition) - 1)) * LPPrev;
            double interest = INm * LPPrev;
            double principal = payment - interest;
            double loanBalance = LPPrev - principal;

            updateSimulationSpecificRow(primeLoanSpitzerRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }




    private void primeLoanFixedPrincipal(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double INmInitial; // Annual loan interest per month

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            INmInitial = (DataManager.PRIME_INTEREST + rout.getInterest()) / 100;  //100 for precentage   annualLoanInterestPerMonth

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                INm = INmInitial / 12;
            } else {  // previous row
                LPPrev = primeLoanFixedPrincipalRows.get(adapterPosition - 1).getLoanBalance();
                INm = Math.min((big / 12 * (adapterPosition) + INmInitial) / 12, cap / 12);
            }

            double interest = INm * LPPrev;
            int YE = rout.getYears();
            double pp = rout.getLoanAmount() / (YE * 12);
            double payment = interest + pp;
            double loanBalance = LPPrev - pp;

            updateSimulationSpecificRow(primeLoanFixedPrincipalRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }


    private void fixedRateFixedPrincipal(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double payment;

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            INm = rout.getInterest() / (100 * 12);  //100 for precentage   annualLoanInterestPerMonth


            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
            } else {  // previous row
                LPPrev = fixedRateFixedPrincipalRows.get(adapterPosition - 1).getLoanBalance();
            }

            double principal = rout.getLoanAmount() / (rout.getYears() * 12);
            double interest = INm * LPPrev;
            payment = principal + interest;
            double loanBalance = LPPrev - principal;

            updateSimulationSpecificRow(fixedRateFixedPrincipalRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }

    private void fixedRateSpitzer(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double payment;

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            INm = rout.getInterest() / (100 * 12);  //100 for precentage   annualLoanInterestPerMonth

            int YE = rout.getYears();

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                payment = ((INm * Math.pow(1 + INm, YE * 12)) / (Math.pow(1 + INm, YE * 12) - 1)) * LPPrev;
            } else {  // previous row
                LPPrev = fixedRateSpitzerRows.get(adapterPosition - 1).getLoanBalance();
                payment = fixedRateSpitzerRows.get(adapterPosition - 1).getPayment();
            }

            double interest = INm * LPPrev;
            double principal = payment - interest;
            double loanBalance = LPPrev - principal;

            updateSimulationSpecificRow(fixedRateSpitzerRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }

    private void nonFixedRateSpitzer(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double payment;

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            //100 for precentage   annualLoanInterestPerMonth
            INm = Math.min((adapterPosition / (rout.changeYears * 12) * INTEREST_GROWTH_PER_CYCLE + rout.getInterest()) / (100 * 12), cap / 12);

            int YE = rout.getYears();

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
            } else {  // previous row
                LPPrev = nonFixedRateSpitzerRows.get(adapterPosition - 1).getLoanBalance();
            }

            payment = ((INm * Math.pow(1 + INm, YE * 12 - adapterPosition)) / (Math.pow(1 + INm, YE * 12 - adapterPosition) - 1)) * LPPrev;  ////

            double interest = INm * LPPrev;
            double principal = payment - interest;
            double loanBalance = LPPrev - principal;

            updateSimulationSpecificRow(nonFixedRateSpitzerRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }

    private void nonFixedRateSpitzerLinked(int adapterPosition, Route rout, SimulationRow simulationRow) {
        double INm;  // interest
        double LPPrev; // loan balance
        double payment;

/*
            if(adapterPosition == 141){
                int a = 1;
                int b = a;
            }
*/

        if (adapterPosition < rout.getYears() * 12) { // within loan months
            //100 for precentage   annualLoanInterestPerMonth
//                double fixedInterestAnnualGrowth = DataManager.getInstance().getSimulationDetails().getFixedInterestAnnualGrowth();
            INm = Math.min((adapterPosition / (rout.changeYears * 12) * fig + rout.getInterest()) / (100 * 12), cap / 12);

            int YE = rout.getYears();

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
            } else {  // previous row
                LPPrev = nonFixedRateSpitzerLinkedRows.get(adapterPosition - 1).getLoanBalance();
            }

            payment = ((INm * Math.pow(1 + INm, YE * 12 - adapterPosition)) / (Math.pow(1 + INm, YE * 12 - adapterPosition) - 1)) * LPPrev;

            double interest = INm * LPPrev;
            double principal = payment - interest;
            double loanBalance = (LPPrev - principal) * (1 + ig);

            updateSimulationSpecificRow(nonFixedRateSpitzerLinkedRows, payment, loanBalance, INm, interest, adapterPosition);
            incrementSimulationTotalRow(simulationRow, payment, loanBalance, INm, interest);
        }
    }







    private void incrementSimulationTotalRow(SimulationRow simulationRow, double payment, double loanBalance, double annualLoanInterestPerMonth, double interest) {
        simulationRow.addAnnualLoanInterestPerMonth(annualLoanInterestPerMonth);
        simulationRow.addInterest(interest);
        simulationRow.addLoanBalance(loanBalance);
        simulationRow.addPayment(payment);
    }

    private void updateSimulationSpecificRow(List<SimulationRow> specificRows, double payment, double loanBalance, double annualLoanInterestPerMonth, double interest, int adapterPosition) {
        SimulationRow row = new SimulationRow();

        row.addAnnualLoanInterestPerMonth(annualLoanInterestPerMonth);
        row.addInterest(interest);
        row.addLoanBalance(loanBalance);
        row.addPayment(payment);

        specificRows.add(row);
    }
}
