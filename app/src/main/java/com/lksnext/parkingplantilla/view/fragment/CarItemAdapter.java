package com.lksnext.parkingplantilla.view.fragment;
import java.util.List;

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

public class CarItemAdapter extends RecyclerView.Adapter<CarItemAdapter.CarViewHolder> {
    private List<Car> carList;
    private OnCarDeleteListener deleteListener;

    public interface OnCarDeleteListener {
        void onCarDeleted(Car car);
    }

    public CarItemAdapter(List<Car> carList, OnCarDeleteListener listener) {
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

        holder.txtTypeMatricula.setText(car.getType().toString().toUpperCase()+"\n" + car.getMatricula());
        holder.txtElectrico.setText("• Eléctrico: " + (car.isElectrico() ? "Sí" : "No"));
        holder.txtDiscapacitados.setText("• Apto Para Discapacitados: " + (car.isParaDiscapacitados() ? "Sí" : "No"));
        String label = car.getEtiqueta().toString();
        if(label.equals("CERO_EMISIONES"))
            label = "\n  Cero Emisiones";
        holder.txtEtiqueta.setText("• Etiqueta Medioambiental: " + label);

        if (car.getType().equals(Car.Type.COCHE))
            holder.carTypeImage.setImageResource(R.drawable.ic_car);
        else
            holder.carTypeImage.setImageResource(R.drawable.ic_motorbike);

        if(!car.isParaDiscapacitados())
            holder.icon1.setVisibility(View.GONE);

        if(!car.isElectrico())
            holder.icon2.setVisibility(View.GONE);

        holder.btnBorrar.setOnClickListener(v -> {
            if(deleteListener != null){
                deleteListener.onCarDeleted(car);
            }
        });

    }

    public void updateList(List<Car> newList){
        carList.clear();
        carList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView txtTypeMatricula;
        TextView txtElectrico;
        TextView txtEtiqueta;
        TextView txtDiscapacitados;
        ImageView carTypeImage;
        ImageView icon1;
        ImageView icon2;
        Button btnBorrar;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTypeMatricula = itemView.findViewById(R.id.txtMatricula);
            txtDiscapacitados = itemView.findViewById(R.id.txtDiscapacitados);
            txtElectrico = itemView.findViewById(R.id.txtElectrico);
            txtEtiqueta = itemView.findViewById(R.id.txtEtiqueta);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
            carTypeImage = itemView.findViewById(R.id.carTypeImage);
            icon1 = itemView.findViewById(R.id.icon1);
            icon2 = itemView.findViewById(R.id.icon2);
        }
    }
}