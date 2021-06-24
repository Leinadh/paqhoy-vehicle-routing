import datetime
import json
import os
import random
from calendar import monthrange
import numpy as np


def crear_arch_pedido(output_path, anho, mes, dia, fecha_ini_global, param_m, param_b, len_eje_x, len_eje_y,
                      posicion_almacen, max_id_cliente):
    current_date = datetime.datetime(year=anho, month=mes, day=dia)
    assert current_date >= fecha_ini_global, 'Error: Fecha es menor a fecha de inicio global'
    dif = current_date - fecha_ini_global
    pred_total_paquetes = round(param_m * dif.days + param_b)
    print(f'{dia=} - T_pedidos={pred_total_paquetes}', end=' | ')

    # Se usara el lado positivo de la distribucion
    media_cant_paquetes = 0
    dev_std_cant_paquetes = 2.5
    # maxima cantidad de paquetes por pedido
    max_cant_paq = 33

    ls_cant_paquetes = []
    total_paquetes = 0
    while total_paquetes != pred_total_paquetes:
        cant_paquetes = np.random.normal(media_cant_paquetes, dev_std_cant_paquetes)
        if cant_paquetes < 0: continue

        cant_paquetes = int(np.floor(cant_paquetes))
        cant_paquetes += 1

        paquetes_faltantes = pred_total_paquetes - total_paquetes
        if cant_paquetes > 3 and paquetes_faltantes > 3:
            max_random = paquetes_faltantes if paquetes_faltantes < max_cant_paq else max_cant_paq
            cant_paquetes = np.random.randint(4, max_random + 1)

        if total_paquetes + cant_paquetes > pred_total_paquetes: continue

        ls_cant_paquetes.append(cant_paquetes)
        total_paquetes += cant_paquetes

    ls_registros_dia = []
    posicion_almacen = (posicion_almacen[0] + 1, posicion_almacen[1] + 1)
    for cant in ls_cant_paquetes:
        pos = None
        while True:
            # En los archivos de pedidos, se indexa de 1 a len_eje (Todo_lo demas se ha hecho de 0 a len_eje-1)
            pos = (random.randrange(len_eje_x) + 1, random.randrange(len_eje_y) + 1)
            if pos != posicion_almacen:
                break

        # hora aleatoria
        rh = random.randrange(24)
        hh = '0' + str(rh) if rh < 10 else str(rh)
        # minuto aleatorio
        rm = random.randrange(60)
        mm = '0' + str(rm) if rm < 10 else str(rm)

        id_cliente = random.randrange(max_id_cliente) + 1  # id cliente comienza en 1

        ls_horas_limite = [24, 2, 3, 4, 8]
        [h_limite] = random.choices(ls_horas_limite, weights=(8, 1, 1, 1, 1))

        ls_registros_dia.append(f'{hh}:{mm},{pos[0]},{pos[1]},{cant},{id_cliente},{h_limite}\n')

    dia_str = '0' + str(dia) if dia < 10 else str(dia)
    mes_str = '0' + str(mes) if mes < 10 else str(mes)
    anho_str = str(anho)
    file_name = f'pedido{anho_str}{mes_str}{dia_str}'
    with open(os.path.join(output_path, file_name), 'w') as file:
        for registro in ls_registros_dia:
            file.write(registro)


if __name__ == '__main__':
    config_file_path = '../config.json'

    with open(config_file_path, 'r') as file:
        config = json.load(file)
    print(config)
    print()

    output_path = '../Test Data/Test Data Sin Formato/pred.2021.pedidos/'

    fecha_ini_global = datetime.datetime(year=2021, month=3, day=14)  # fecha de inicio de regresion
    # parametros de regresion
    param_m = 5.2780198
    param_b = 105.6580198

    max_id_cliente = 900

    anho = 2021
    ini_mes = 6
    ini_dia = 22
    fin_mes = 12
    for mes in range(ini_mes, fin_mes + 1):
        if mes != ini_mes: ini_dia = 1
        print(f'{mes=}')
        for dia in range(ini_dia, monthrange(anho, mes)[1] + 1):
            crear_arch_pedido(output_path, anho, mes, dia, fecha_ini_global, param_m, param_b, config['LONGITUD_EJE_X'],
                              config['LONGITUD_EJE_Y'], config['POSICION_ALMACEN'], max_id_cliente)
            # break
        # break
        print()
