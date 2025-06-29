package com.lksnext.parkingplantilla.domain;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lksnext.parkingplantilla.R;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private ArrayList<Car> CarList;

    public CarAdapter(ArrayList<Car> CarList) {
        this.CarList = CarList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_car_item, parent, false);
        return new CarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = CarList.get(position);

        holder.txtMatricula.setText("Matrícula: " + car.getMatricula());
        holder.txtTipo.setText("Tipo: " + car.getType().toString().replace('_',' '));
        holder.txtElectrico.setText("Eléctrico: " + (car.isElectrico() ? "Sí" : "No"));
        holder.txtEtiqueta.setText("Etiqueta: " + car.getEtiqueta().toString().replace('_',' '));

        holder.btnBorrar.setOnClickListener(v -> {
            CarList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, CarList.size());
        });
    }

    @Override
    public int getItemCount() {
        return CarList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView txtMatricula, txtTipo, txtElectrico, txtEtiqueta;
        Button btnBorrar;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMatricula = itemView.findViewById(R.id.txtMatricula);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtElectrico = itemView.findViewById(R.id.txtElectrico);
            txtEtiqueta = itemView.findViewById(R.id.txtEtiqueta);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
        }
    }
}

