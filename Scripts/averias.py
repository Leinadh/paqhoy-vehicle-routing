from calendar import monthrange
import random
import os


def crear_arch_averias(output_path, anho, mes, cant_motos, cant_autos):
    vehiculos = ['moto', 'auto']
    cant_averias = 50
    vehiculos_averiados = random.choices(vehiculos, weights=(cant_motos, cant_autos), k=cant_averias)

    averias_mes = []

    for vehiculo in vehiculos_averiados:
        # dia aleatorio en el mes
        rd = random.randrange(monthrange(anho, mes)[1]) + 1
        dd = '0' + str(rd) if rd < 10 else str(rd)

        # id aleatorio
        cant_vehiculo, ini_id = (cant_motos, 1) if vehiculo == 'moto' else (cant_autos + cant_motos, cant_motos + 1)
        ri = random.randrange(ini_id, cant_vehiculo + 1)
        id_vehiculo = '0' + str(ri) if ri < 10 else str(ri)

        # hora aleatoria
        rh = random.randrange(24)
        hh = '0' + str(rh) if rh < 10 else str(rh)

        # minuto aleatorio
        rm = random.randrange(60)
        mm = '0' + str(rm) if rm < 10 else str(rm)

        registro_averia = f'{dd}.{vehiculo}.{id_vehiculo}.{hh}:{mm}'
        # print(registro_averia)
        averias_mes.append(registro_averia)

    # ordenar por dia y hora
    averias_mes.sort(key=lambda x: (x[:2], x[11:13], x[14:]))
    # print(averias_mes)

    mes_str = '0' + str(mes) if mes < 10 else str(mes)
    anho_str = str(anho)
    file_name = f'averias.{anho_str}{mes_str}'
    with open(os.path.join(output_path, file_name), 'w') as file:
        for registro in averias_mes:
            file.write(registro)
            file.write('\n')


if __name__ == '__main__':
    output_path = '../Test Data/averias.2021/'

    cant_motos = 40
    cant_autos = 20

    anho = 2021

    # se crean 50 registros por mes
    for mes in range(1, 13):
        crear_arch_averias(output_path, anho, mes, cant_motos, cant_autos)
