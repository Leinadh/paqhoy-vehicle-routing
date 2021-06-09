import json
import os
import random
from calendar import monthrange
import numpy as np
import datetime


def existe_interseccion_periodos(periodo_1, periodo_2):
    # cada periodo debe ser un string con la siguiente estructura: dd:hh:mm-dd:hh:mm

    ini_dt_1 = datetime.datetime(year=2021, month=1, day=int(periodo_1[:2]), hour=int(periodo_1[3:5]),
                                 minute=int(periodo_1[6:8]))
    fin_dt_1 = datetime.datetime(year=2021, month=1, day=int(periodo_1[9:11]), hour=int(periodo_1[12:14]),
                                 minute=int(periodo_1[15:17]))
    ini_dt_2 = datetime.datetime(year=2021, month=1, day=int(periodo_2[:2]), hour=int(periodo_2[3:5]),
                                 minute=int(periodo_2[6:8]))
    fin_dt_2 = datetime.datetime(year=2021, month=1, day=int(periodo_2[9:11]), hour=int(periodo_2[12:14]),
                                 minute=int(periodo_2[15:17]))

    if fin_dt_1 >= ini_dt_2 and ini_dt_1 <= fin_dt_2:
        return True
    return False


def existe_interseccion_punto_secuencia(pos, secuencia_nodos):
    for i in [-1, 0, 1]:
        for j in [-1, 0, 1]:
            if (pos[0] + i, pos[1] + j) in secuencia_nodos:
                return True
    return False


def crear_secuencia_nodos_bloqueados(len_eje_x, len_eje_y, posicion_almacen, max_cant_bloqueos,
                                     ls_secuencias_nodos_mes, periodo_tiempo_actual):
    ls_nodos_bloqueados = []

    posicion_almacen = tuple(posicion_almacen)
    ls_nodos_bloqueados.append(posicion_almacen)

    # posicion inicial de bloqueo no es la posicion del almacen
    ini_pos = None
    while (True):
        ini_pos = (random.randrange(len_eje_x), random.randrange(len_eje_y))
        for periodo, secuencia_nodos in ls_secuencias_nodos_mes:
            if existe_interseccion_periodos(periodo, periodo_tiempo_actual) and \
                    existe_interseccion_punto_secuencia(ini_pos, secuencia_nodos):
                continue
        if ini_pos not in ls_nodos_bloqueados:
            break
    ls_nodos_bloqueados.append(ini_pos)

    for i in range(2, max_cant_bloqueos + 1):
        ls_directions = [('x', 1), ('x', -1), ('y', 1), ('y', -1)]
        random.shuffle(ls_directions)

        encontrado = False
        for direc in ls_directions:
            last_pos = ls_nodos_bloqueados[-1]
            if direc[0] == 'x':
                new_pos = (last_pos[0] + direc[1], last_pos[1])
                sig_new_pos = [(last_pos[0] + direc[1] * 2, last_pos[1]),
                               (last_pos[0] + direc[1] * 2, last_pos[1] + 1),
                               (last_pos[0] + direc[1] * 2, last_pos[1] - 1),
                               (last_pos[0] + direc[1], last_pos[1] + 1),
                               (last_pos[0] + direc[1], last_pos[1] - 1)]
            else:
                new_pos = (last_pos[0], last_pos[1] + direc[1])
                sig_new_pos = [(last_pos[0], last_pos[1] + direc[1] * 2),
                               (last_pos[0] + 1, last_pos[1] + direc[1] * 2),
                               (last_pos[0] - 1, last_pos[1] + direc[1] * 2),
                               (last_pos[0] + 1, last_pos[1] + direc[1]),
                               (last_pos[0] - 1, last_pos[1] + direc[1])]

            # No permitir que haya posiciones a las que no se pueda llegar
            if new_pos[0] not in range(1, len_eje_x - 1) or new_pos[1] not in range(1, len_eje_y - 1):
                continue

            if new_pos in ls_nodos_bloqueados:
                continue
            for periodo, secuencia_nodos in ls_secuencias_nodos_mes:
                if existe_interseccion_periodos(periodo, periodo_tiempo_actual) and new_pos in secuencia_nodos:
                    continue

            sig_bloqueado = False
            for i_sig_new_pos in sig_new_pos:
                if i_sig_new_pos in ls_nodos_bloqueados:
                    sig_bloqueado = True
                    break

                bloq_secuencias_mes = False
                for periodo, secuencia_nodos in ls_secuencias_nodos_mes:
                    if existe_interseccion_periodos(periodo, periodo_tiempo_actual) \
                            and i_sig_new_pos in secuencia_nodos:
                        bloq_secuencias_mes = True
                        break
                if bloq_secuencias_mes:
                    sig_bloqueado = True
                    break
            if sig_bloqueado: continue

            encontrado = True
            ls_nodos_bloqueados.append(new_pos)
            break

        if not encontrado:
            # Nunca se encuentra siguiente nodo bloqueado para ini_pos en una esquina del mapa
            print(f'Nodo bloqueado {i} no encontrado. Ultima posicion bloqueada: {last_pos}')
            break

    print(ls_nodos_bloqueados)

    # se desbloquea almacen
    ls_nodos_bloqueados.pop(0)

    return ls_nodos_bloqueados


def crear_arch_calles_bloqueadas(output_path, anho, mes, len_eje_x, len_eje_y, posicion_almacen,
                                 max_cant_bloqueos_x_registro, max_cant_registros, media_rango_t, dev_std_rango_t):
    ls_rangos_tiempo = np.random.normal(media_rango_t, dev_std_rango_t, max_cant_registros)

    # lista de secuencias de nodos bloqueados. Usada para verificar que nuevas secuencias no interfieran entre si
    ls_secuencias_nodos_mes = []

    ls_registros = []

    for rango_tiempo in ls_rangos_tiempo:
        # dia aleatorio en el mes
        cant_dias_mes = monthrange(anho, mes)[1]
        rd = random.randrange(cant_dias_mes) + 1
        # hora aleatoria
        rh = random.randrange(24)
        # minuto aleatorio
        rm = random.randrange(60)

        # definir inicio y fin del periodo
        ini_periodo = datetime.datetime(year=anho, month=mes, day=rd, hour=rh, minute=rm)
        if rango_tiempo <= 0: continue
        fin_periodo = ini_periodo + datetime.timedelta(hours=rango_tiempo)
        if fin_periodo.month != ini_periodo.month:
            fin_periodo = datetime.datetime(year=anho, month=mes, day=cant_dias_mes, hour=23, minute=59)
        if ini_periodo == fin_periodo: continue

        ini_dd = '0' + str(ini_periodo.day) if ini_periodo.day < 10 else str(ini_periodo.day)
        ini_hh = '0' + str(ini_periodo.hour) if ini_periodo.hour < 10 else str(ini_periodo.hour)
        ini_mm = '0' + str(ini_periodo.minute) if ini_periodo.minute < 10 else str(ini_periodo.minute)
        fin_dd = '0' + str(fin_periodo.day) if fin_periodo.day < 10 else str(fin_periodo.day)
        fin_hh = '0' + str(fin_periodo.hour) if fin_periodo.hour < 10 else str(fin_periodo.hour)
        fin_mm = '0' + str(fin_periodo.minute) if fin_periodo.minute < 10 else str(fin_periodo.minute)

        # definir periodo de tiempo en que la secuencia de nodos estara bloqueada
        periodo_tiempo = f'{ini_dd}:{ini_hh}:{ini_mm}-{fin_dd}:{fin_hh}:{fin_mm}'

        # generar secuencia de nodos bloqueados
        ls_nodos_bloqueados = crear_secuencia_nodos_bloqueados(len_eje_x, len_eje_y, posicion_almacen,
                                                               max_cant_bloqueos_x_registro, ls_secuencias_nodos_mes,
                                                               periodo_tiempo)

        # Crear un registro
        ls_secuencias_nodos_mes.append((periodo_tiempo, ls_nodos_bloqueados))
        registro = periodo_tiempo
        for nodo in ls_nodos_bloqueados:
            registro += f',{nodo[0]},{nodo[1]}'
        ls_registros.append(registro)

    print(ls_registros)
    print()

    mes_str = '0' + str(mes) if mes < 10 else str(mes)
    anho_str = str(anho)
    file_name = f'{anho_str}{mes_str}.bloqueadas'
    with open(os.path.join(output_path, file_name), 'w') as file:
        for registro in ls_registros:
            file.write(registro + '\n')


def main():
    MEDIA_RANGO_T = 10
    DEV_STD_RANGO_T = 15
    MAX_CANT_REGISTROS = 60

    output_path = '../Test Data/2021.bloqueadas/'

    config_file_path = '../config.json'

    with open(config_file_path, 'r') as file:
        config = json.load(file)
    print(config)
    print()

    max_cant_bloqueos_x_registro = 70
    anho = 2021
    for mes in range(1, 13):
        crear_arch_calles_bloqueadas(output_path, anho, mes, config['LONGITUD_EJE_X'], config['LONGITUD_EJE_Y'],
                                     config['POSICION_ALMACEN'], max_cant_bloqueos_x_registro, MAX_CANT_REGISTROS,
                                     MEDIA_RANGO_T, DEV_STD_RANGO_T)


if __name__ == '__main__':
    main()
