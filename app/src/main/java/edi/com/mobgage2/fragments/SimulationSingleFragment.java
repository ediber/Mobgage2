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
            big = simulationDetails.getBankIsraelAnnualGrowth() / 100;
            fig = simulationDetails.getFixedInterestAnnualGrowth();
//            =(1+D33)^(1/12)-1
            ig = simulationDetails.getIndexAnnualGrowth() / (100 * 12);
            cap = simulationDetails.getCapInterest() / 100;

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

            for (int i = 0; i < proposal.getRoutes().size(); i++) {
                calculateRoute(i, position, simulationRow);
            }

            if (rows.size() > position) {
                rows.remove(position);
            }
            rows.add(position, simulationRow);

            holder.month_return.setText(NumberUtils.doubleToMoney(simulationRow.getPayment()));
            holder.interest.setText(NumberUtils.doubleToMoney(simulationRow.getInterest()));
            if (position > 0) {
                holder.left.setText(NumberUtils.doubleToMoney(rows.get(position - 1).getLoanBalance())); // display previous balance
            } else {
                holder.left.setText(NumberUtils.doubleToMoney(proposal.getTotalRepayment())); // display previous balance

            }

//                DataManager.getInstance().getRouteKindByID(rout.routeKind);

        }

        @NonNull
        private void calculateRoute(int routeIndex, int adapterPosition, SimulationRow simulationRow) {
            Route rout = proposal.getRoutes().get(routeIndex);
            if (rout.getReturnMethod() == 1) { // spitzer

                switch (rout.getRouteKind()) {
                    case 0: // ribit prime -- prime loan spitzer
//                        primeLoanSpitzer(adapterPosition, rout, simulationRow);
                        break;
                    case 1: // ribit kvua tsmuda lamadad -- fixed rate spitzer linked
//                        fixedRateSpitzerLinked(adapterPosition, rout, simulationRow);
                        break;
                    case 2: // ribit kvua lo tsmuda -- fixed rate spitzer
//                        fixedRateSpitzer(adapterPosition, rout, simulationRow);
                        break;

                    case 3:
                        nonFixedRateSpitzer(adapterPosition, rout, simulationRow);
                        break;


                }

            } else if (rout.getReturnMethod() == 0) { // keren shava
                switch (rout.getRouteKind()) {
                    case 0: // prime loan - fixed prinsipal
//                        primeLoanFixedPrincipal(adapterPosition, rout, simulationRow);
                        break;
                    case 1: // fixed rate - fixed principal linked
//                        fixedRateFixedPrincipalLinked(adapterPosition, rout, simulationRow);
                        break;
                    case 2: // fixed rate - fixed principal
//                        fixedRateFixedPrincipal(adapterPosition, rout, simulationRow);
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
                    LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                    //  Pmt*(1+Ig)^(n-1)
                    double firstPayment = rows.get(0).getPayment();
                    payment = firstPayment * Math.pow(1 + ig, adapterPosition);
                }

                double interest = INm * LPPrev;
                double principal = payment - interest;
                double loanBalance = LPPrev - principal;

                incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);
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
                    LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                    // PP*(1+Ig)^(n-1)
                    principal = pp * Math.pow(1 + ig, adapterPosition);
                }

//
                double interest = INm * LPPrev;
                payment = principal + interest;
                double loanBalance = (LPPrev - principal) * (1 + ig);

                incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);
            }
        }

        private void primeLoanSpitzer(int adapterPosition, Route rout, SimulationRow simulationRow) {
            double INm;  // interest
            double LPPrev; // loan balance
            double INmInitial; // Annual loan interest per month


            INmInitial = (DataManager.PRIME_INTEREST + rout.getInterest()) / 100;  //100 for precentage   annualLoanInterestPerMonth

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                INm = INmInitial / 12;
            } else {  // previous row
                LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                INm = Math.min((big / 12 * (adapterPosition) + INmInitial) / 12, cap / 12);
            }

            int YE = rout.getYears();

            double payment = ((INm * Math.pow(1 + INm, YE * 12 - adapterPosition)) / (Math.pow(1 + INm, YE * 12 - adapterPosition) - 1)) * LPPrev;
            double interest = INm * LPPrev;
            double principal = payment - interest;
            double loanBalance = LPPrev - principal;

            incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);

        }


        private void primeLoanFixedPrincipal(int adapterPosition, Route rout, SimulationRow simulationRow) {
            double INm;  // interest
            double LPPrev; // loan balance
            double INmInitial; // Annual loan interest per month

            INmInitial = (DataManager.PRIME_INTEREST + rout.getInterest()) / 100;  //100 for precentage   annualLoanInterestPerMonth

            if (adapterPosition == 0) {
                LPPrev = rout.getLoanAmount();
                INm = INmInitial / 12;
            } else {  // previous row
                LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                INm = Math.min((big / 12 * (adapterPosition) + INmInitial) / 12, cap / 12);
            }

            double interest = INm * LPPrev;
            int YE = rout.getYears();
            double pp = rout.getLoanAmount() / (YE * 12);
            double payment = interest + pp;
            double loanBalance = LPPrev - pp;

            incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);
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
                    LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                }

                double principal = rout.getLoanAmount() / (rout.getYears() * 12);
                double interest = INm * LPPrev;
                payment = principal + interest;
                double loanBalance = LPPrev - principal;

                incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);
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
                    LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                    payment = rows.get(adapterPosition - 1).getPayment();
                }

                double interest = INm * LPPrev;
                double principal = payment - interest;
                double loanBalance = LPPrev - principal;

                incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);
            }
        }

        private void nonFixedRateSpitzer(int adapterPosition, Route rout, SimulationRow simulationRow) {
            double INm;  // interest
            double LPPrev; // loan balance
            double payment;

            if (adapterPosition < rout.getYears() * 12) { // within loan months
                //100 for precentage   annualLoanInterestPerMonth
                INm = Math.min((adapterPosition / (rout.changeYears * 12) * DataManager.INTEREST_GROWTH_PER_CYCLE + rout.getInterest()) / (100 * 12), cap / 12);

                int YE = rout.getYears();

                if (adapterPosition == 0) {
                    LPPrev = rout.getLoanAmount();
                } else {  // previous row
                    LPPrev = rows.get(adapterPosition - 1).getLoanBalance();
                }

                payment = ((INm * Math.pow(1 + INm, YE * 12 - adapterPosition)) / (Math.pow(1 + INm, YE * 12 - adapterPosition) - 1)) * LPPrev;  ////

                double interest = INm * LPPrev;
                double principal = payment - interest;
                double loanBalance = LPPrev - principal;

                incrementSimulationRow(simulationRow, payment, loanBalance, INm, interest);
            }
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
            }
        }
    }


}
