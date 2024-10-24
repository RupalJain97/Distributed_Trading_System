
function updateMetricsFromResponse(data) {
    console.log(data);

    // Update the charts
    const currentTime = new Date().toLocaleTimeString();
    threadBarChart.data.datasets[0].data = [
        data.totalThreads,
        data.runningThreads,
        data.peakThreads
    ];
    threadBarChart.update();

    cpuChart.data.labels.push(currentTime);
    cpuChart.data.datasets[0].data.push(data.systemLoad);
    cpuChart.update();

    memoryChart.data.labels.push(currentTime);
    memoryChart.data.datasets[0].data.push(data.heapMemoryUsage);
    memoryChart.data.datasets[1].data.push(data.nonHeapMemoryUsage);
    memoryChart.update();
}
