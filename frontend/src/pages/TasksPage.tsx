import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Navbar } from "../components/Navbar";
import { TaskCard } from "../components/TaskCard";
import { TaskForm } from "../components/TaskForm";
import { getErrorMessage } from "../api/client";
import * as tasksApi from "../api/tasks";
import type { Task, TaskStatus } from "../types";
import type { TaskInput } from "../api/tasks";

export function TasksPage() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<TaskStatus | "">("");
  const [search, setSearch] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | null>(null);

  useEffect(() => {
    const timeout = setTimeout(() => {
      loadTasks();
    }, 300);
    return () => clearTimeout(timeout);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [statusFilter, search]);

  async function loadTasks() {
    setLoading(true);
    try {
      const data = await tasksApi.fetchTasks(statusFilter, search);
      setTasks(data);
    } catch (error) {
      toast.error(getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }

  async function handleCreate(input: TaskInput) {
    try {
      const created = await tasksApi.createTask(input);
      setTasks((prev) => [created, ...prev]);
      setShowForm(false);
      toast.success("task added");
    } catch (error) {
      toast.error(getErrorMessage(error));
    }
  }

  async function handleUpdate(input: TaskInput) {
    if (!editingTask) return;
    try {
      const updated = await tasksApi.updateTask(editingTask.id, input);
      setTasks((prev) => prev.map((t) => (t.id === updated.id ? updated : t)));
      setEditingTask(null);
      toast.success("task updated");
    } catch (error) {
      toast.error(getErrorMessage(error));
    }
  }

  async function handleDelete(id: number) {
    if (!confirm("Delete this task?")) return;
    try {
      await tasksApi.deleteTask(id);
      setTasks((prev) => prev.filter((t) => t.id !== id));
      toast.success("task deleted");
    } catch (error) {
      toast.error(getErrorMessage(error));
    }
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <Navbar />

      <main className="mx-auto max-w-4xl px-4 py-6">
        <div className="mb-4 flex flex-wrap items-center gap-2">
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search tasks..."
            className="flex-1 min-w-[180px] rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none"
          />
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as TaskStatus | "")}
            className="rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none"
          >
            <option value="">All statuses</option>
            <option value="TODO">To do</option>
            <option value="IN_PROGRESS">In progress</option>
            <option value="DONE">Done</option>
          </select>
          <button
            onClick={() => {
              setEditingTask(null);
              setShowForm((v) => !v);
            }}
            className="rounded-md bg-slate-800 px-3 py-2 text-sm font-medium text-white hover:bg-slate-700"
          >
            {showForm ? "Close" : "+ New task"}
          </button>
        </div>

        {showForm && (
          <div className="mb-4">
            <TaskForm onSubmit={handleCreate} onCancel={() => setShowForm(false)} />
          </div>
        )}

        {loading ? (
          <p className="text-sm text-slate-500">Loading tasks...</p>
        ) : tasks.length === 0 ? (
          <p className="text-sm text-slate-500">No tasks found.</p>
        ) : (
          <div className="space-y-3">
            {tasks.map((task) =>
              editingTask?.id === task.id ? (
                <TaskForm
                  key={task.id}
                  initialTask={task}
                  onSubmit={handleUpdate}
                  onCancel={() => setEditingTask(null)}
                />
              ) : (
                <TaskCard
                  key={task.id}
                  task={task}
                  onEdit={() => {
                    setShowForm(false);
                    setEditingTask(task);
                  }}
                  onDelete={() => handleDelete(task.id)}
                />
              )
            )}
          </div>
        )}
      </main>
    </div>
  );
}
