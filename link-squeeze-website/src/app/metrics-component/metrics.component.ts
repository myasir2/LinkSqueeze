import {Component, Inject, OnInit} from "@angular/core"
import {MAT_DIALOG_DATA, MatDialogContent, MatDialogRef, MatDialogTitle} from "@angular/material/dialog"
import {MatDividerModule} from "@angular/material/divider"
import {MatIconModule} from "@angular/material/icon"
import {BackendApiService} from "../../service/backend-api.service"
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner"
import {NgIf} from "@angular/common"
import {MatTableModule} from "@angular/material/table"
import _ from "lodash"

export interface MetricsMetadata {
  readonly urlHash: string;
}

interface GroupedMetrics {
  readonly metricType: string;
  readonly value: number
}

@Component({
    selector: "app-metrics-component",
    standalone: true,
    imports: [
        MatDialogTitle,
        MatDialogContent,
        MatDividerModule,
        MatIconModule,
        MatProgressSpinnerModule,
        NgIf,
        MatTableModule
    ],
    templateUrl: "./metrics.component.html",
    styleUrl: "./metrics.component.scss",
})
export class MetricsComponent implements OnInit {

    urlHash: string
    isLoading = false
    groupedMetrics: GroupedMetrics[] = []
    displayedColumns: string[] = ["metricType", "value"]

    constructor(
    @Inject(MAT_DIALOG_DATA) private data: MetricsMetadata,
    private api: BackendApiService,
    public dialogRef: MatDialogRef<MetricsComponent>
    ) {
        this.urlHash = data.urlHash
    }

    ngOnInit(): void {
        this.getMetrics()
    }

    closeDialog(): void {
        this.dialogRef.close()
    }

    private async getMetrics() {
        this.isLoading = true

        const metrics = await this.api.getUrlMetrics(this.urlHash)
        this.groupedMetrics = _(metrics)
            .groupBy("metricType")
            .map((group, metricType) => ({
                metricType,
                value: _.sumBy(group, "count"),
            }))
            .value()

        this.isLoading = false
    }
}
