
% Create a new figure for Replicas_D plot
figure;
hold on;

% Plot each column with different linestyle
for i = 1:size(TiempoProcesamiento_A, 2)
    plot(Tiempo_A, TiempoProcesamiento_A(:, i), 'LineWidth', 1);
end

% Customize the plot
xlabel('Tiempo (s)');
ylabel('Tiempo de Procesamiento (s)');
title('Tiempo Medio de Procesamiento');
grid on;
legend('Tiempo','Réplica 2','Réplica 3','Réplica 4','Réplica 5');
hold off;
