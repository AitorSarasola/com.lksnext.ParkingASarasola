package com.lksnext.parkingplantilla.view.fragment;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Car;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private ArrayList<Car> carList;
    private OnCarDeleteListener deleteListener;

    public interface OnCarDeleteListener {
        void onCarDeleted(Car car);
    }

    public CarAdapter(ArrayList<Car> carList, OnCarDeleteListener listener) {
        this.carList = carList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_car_item, parent, false);
        return new CarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);

        holder.txtMatricula.setText("Matrícula:\n" + car.getMatricula());
        holder.txtTipo.setText(car.getType().toString().replace('_',' '));
        holder.txtElectrico.setText("Eléctrico: " + (car.isElectrico() ? "Sí" : "No"));
        String label = car.getEtiqueta().toString().replace('_',' ');
        if(label.length()>5)
            label = "\n"+label;
        holder.txtEtiqueta.setText("Etiqueta Medioambiental: " + label);

        switch (car.getType()) {
            case Coche:
                holder.carTypeImage.setImageResource(R.drawable.ic_car);
                break;
            case Moto:
                holder.carTypeImage.setImageResource(R.drawable.ic_motorbike);
                break;
            case Coche_Para_Discapacitados:
                holder.carTypeImage.setImageResource(R.drawable.ic_disabled_car);
                break;
        }

        if(!car.isElectrico()){
            holder.carElectric.setImageResource(0);
        }

        holder.btnBorrar.setOnClickListener(v -> {
            if(deleteListener != null){
                deleteListener.onCarDeleted(car);
            }
        });

    }

    public void updateList(ArrayList<Car> newList){
        carList.clear();
        carList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView txtMatricula, txtTipo, txtElectrico, txtEtiqueta;
        ImageView carTypeImage, carElectric;
        Button btnBorrar;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMatricula = itemView.findViewById(R.id.txtMatricula);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtElectrico = itemView.findViewById(R.id.txtElectrico);
            txtEtiqueta = itemView.findViewById(R.id.txtEtiqueta);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
            carTypeImage = itemView.findViewById(R.id.carTypeImage);
            carElectric = itemView.findViewById(R.id.carElectric);
        }
    }
}

