import os


def formatear_arch_calles_bloqueadas(input_file, input_file_name, output_path):
    registros_formateados = []
    with open(input_file, 'r') as f:
        for line in f:
            line = line.strip()
            # print(line)  ######################
            split_line = line.split(',')

            nodos = [split_line[1], split_line[2]]
            for i in range(1, len(split_line) - 2, 2):
                # print(f'{split_line[i]},{split_line[i + 1]}-{split_line[i + 2]},{split_line[i + 3]}', end='')
                assert int(split_line[i]) == int(split_line[i + 2]) or \
                       int(split_line[i + 1]) == int(split_line[i + 3]), 'Los nodos no son una esquina'
                if int(split_line[i]) == int(split_line[i + 2]):
                    delta_y = int(split_line[i + 3]) - int(split_line[i + 1])
                    # print(f' -> delta y = {delta_y}')
                    if delta_y < 0:
                        rango_delta_y = range(-1, delta_y - 1, -1)

                    else:
                        rango_delta_y = range(1, delta_y + 1)

                    for j in rango_delta_y:
                        # print(f',{split_line[i]},{int(split_line[i + 1]) + j}', end='')
                        nodos.append(split_line[i])
                        nodos.append(str(int(split_line[i + 1]) + j))

                else:
                    delta_x = int(split_line[i + 2]) - int(split_line[i])
                    # print(f' -> delta x = {delta_x}')

                    if delta_x < 0:
                        rango_delta_x = range(-1, delta_x - 1, -1)
                    else:
                        rango_delta_x = range(1, delta_x + 1)

                    for j in rango_delta_x:
                        # print(f',{int(split_line[i]) + j},{split_line[i + 1]}', end='')
                        nodos.append(str(int(split_line[i]) + j))
                        nodos.append(split_line[i + 1])

                # print()
            new_line = split_line[0] + ',' + ','.join(nodos)
            # print(new_line)  #################################
            registros_formateados.append(new_line)

    print(registros_formateados)
    print()

    with open(os.path.join(output_path, input_file_name), 'w') as f:
        for registro in registros_formateados:
            f.write(registro)
            f.write('\n')


if __name__ == '__main__':
    input_path = '../Test Data/Test Data Sin Formato/bloqueos/'
    output_path = '../Test Data/formateado.data.20210710/formateado.bloqueos.20210710'

    for file_name in os.listdir(input_path):
        print(file_name)
        formatear_arch_calles_bloqueadas(os.path.join(input_path, file_name), file_name, output_path)
