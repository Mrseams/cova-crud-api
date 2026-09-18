import type { Task } from "../types";
import { StatusBadge } from "./StatusBadge";

interface TaskCardProps {
  task: Task;
  onEdit: () => void;
  onDelete: () => void;
}

export function TaskCard({ task, onEdit, onDelete }: TaskCardProps) {
  return (
    <div className="flex items-start justify-between gap-4 rounded-lg border border-slate-200 bg-white p-4">
      <div className="min-w-0">
        <div className="flex items-center gap-2">
          <h3 className="truncate font-medium text-slate-800">{task.title}</h3>
          <StatusBadge status={task.status} />
        </div>
        {task.description && (
          <p className="mt-1 text-sm text-slate-500">{task.description}</p>
        )}
        <p className="mt-2 text-xs text-slate-400">
          updated {new Date(task.updatedAt).toLocaleString()}
        </p>
      </div>

      <div className="flex shrink-0 gap-2">
        <button
          onClick={onEdit}
          className="rounded-md border border-slate-300 px-2.5 py-1 text-xs hover:bg-slate-50"
        >
          Edit
        </button>
        <button
          onClick={onDelete}
          className="rounded-md border border-red-200 px-2.5 py-1 text-xs text-red-600 hover:bg-red-50"
        >
          Delete
        </button>
      </div>
    </div>
  );
}
