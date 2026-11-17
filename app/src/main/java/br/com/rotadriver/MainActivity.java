package br.com.rotadriver;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.content.SharedPreferences;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    int bg = Color.rgb(12,16,24), panel = Color.rgb(24,31,43), accent = Color.rgb(36,190,150), white = Color.WHITE, muted = Color.rgb(165,178,195);
    SharedPreferences prefs;
    LinearLayout root, history;
    EditText amount, note;
    Spinner category;
    TextView total, expenses, net, log;
    NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
    double income=0, costs=0;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("rota",MODE_PRIVATE);
        income=Double.longBitsToDouble(prefs.getLong("income",Double.doubleToLongBits(0)));
        costs=Double.longBitsToDouble(prefs.getLong("costs",Double.doubleToLongBits(0)));
        build();
    }
    TextView text(String s,int size,int color,boolean bold) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if(bold)t.setTypeface(null,Typeface.BOLD); t.setPadding(0,5,0,5); return t;
    }
    void build() {
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(bg);
        root=new LinearLayout(this); root.setOrientation(1); root.setPadding(20,24,20,28); scroll.addView(root); setContentView(scroll);
        root.addView(text("ROTADRIVER",25,white,true));
        root.addView(text("Seu controle financeiro na estrada",14,muted,false));
        LinearLayout cards=new LinearLayout(this); cards.setOrientation(1); cards.setPadding(16,12,16,12); cards.setBackgroundColor(panel);
        total=text("Ganhos: "+money.format(income),18,white,true); expenses=text("Despesas: "+money.format(costs),16,muted,false); net=text("Lucro líquido: "+money.format(income-costs),20,accent,true);
        cards.addView(total); cards.addView(expenses); cards.addView(net); root.addView(cards);
        root.addView(text("Nova movimentação",20,white,true));
        category=new Spinner(this);
        String[] options={"Ganho Uber","Ganho 99","Ganho inDrive","Combustível","Outra despesa"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,options); category.setAdapter(adapter); root.addView(category);
        amount=new EditText(this); amount.setSingleLine(true); amount.setHint("Valor em reais (ex.: 35,50)"); amount.setInputType(8194); amount.setTextColor(white); amount.setHintTextColor(muted); root.addView(amount);
        note=new EditText(this); note.setSingleLine(true); note.setHint("Observação (opcional)"); note.setTextColor(white); note.setHintTextColor(muted); root.addView(note);
        Button add=new Button(this); add.setText("ADICIONAR LANÇAMENTO"); add.setOnClickListener(v->save()); root.addView(add);
        Button reset=new Button(this); reset.setText("ZERAR RESUMO"); reset.setOnClickListener(v->{income=0;costs=0;log.setText("Histórico limpo.");persist();refresh();}); root.addView(reset);
        root.addView(text("Histórico",20,white,true)); log=text(prefs.getString("log","Nenhum lançamento ainda."),14,muted,false); root.addView(log);
        refresh();
    }
    void save() {
        String raw=amount.getText().toString().trim().replace(",",".");
        if(raw.isEmpty()){amount.setError("Informe um valor");return;}
        double v; try{v=Double.parseDouble(raw);}catch(Exception e){amount.setError("Valor inválido");return;}
        if(v<=0){amount.setError("O valor deve ser maior que zero");return;}
        String type=category.getSelectedItem().toString();
        boolean earning=type.startsWith("Ganho");
        if(earning)income+=v;else costs+=v;
        String line=type+" • "+money.format(v)+(note.getText().length()>0?" • "+note.getText():"");
        String old=prefs.getString("log","");
        String updated=line+"\n"+(old.equals("Nenhum lançamento ainda.")?"":old);
        log.setText(updated); amount.setText(""); note.setText(""); persist(); refresh();
    }
    void persist(){prefs.edit().putLong("income",Double.doubleToLongBits(income)).putLong("costs",Double.doubleToLongBits(costs)).putString("log",log.getText().toString()).apply();}
    void refresh(){if(total!=null){total.setText("Ganhos: "+money.format(income));expenses.setText("Despesas: "+money.format(costs));net.setText("Lucro líquido: "+money.format(income-costs));}}
}
